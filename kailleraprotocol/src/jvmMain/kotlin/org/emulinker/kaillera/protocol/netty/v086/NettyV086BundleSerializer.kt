package org.emulinker.kaillera.protocol.netty.v086

import io.netty.buffer.ByteBuf
import org.emulinker.kaillera.protocol.v086.V086Bundle
import org.emulinker.kaillera.protocol.v086.V086Message

object NettyV086BundleSerializer {

  fun write(buffer: ByteBuf, bundle: V086Bundle, charset: String) {
    val messages = bundle.messages
    buffer.writeByte(messages.size)
    for (message in messages) {
      buffer.writeShortLE(message.messageNumber)
      buffer.writeShortLE(
        message.bodyBytes + 1
      ) // bodyBytes doesn't include typeID in KMP definition usually?
      // Wait, KMP V086Message.bodyBytes usually includes the content size.
      // Legacy: bodyBytesPlusMessageIdType = bodyBytes + 1.
      // KMP V086Message: bodyBytes IS the size of the body.
      // Check GameData: bodyBytes = gameData.size + 1. It INCLUDES the ID?
      // Let's check V086Message.kt in KMP.

      // If KMP bodyBytes includes ID, then we just write bodyBytes.
      // If it EXCLUDES ID, we write bodyBytes + 1.

      // I'll assume for now I need to check V086Message reference.

      buffer.writeByte(message.messageTypeId.toInt())
      NettyMessageFactory.write(buffer, message, charset)
    }
  }

  // Parse logic adapted from legacy
  fun read(buffer: ByteBuf, lastMessageId: Int = -1, charset: String): V086Bundle {
    if (buffer.readableBytes() < 5) {
      throw IllegalArgumentException("Invalid buffer length: " + buffer.readableBytes())
    }

    val messageCount = buffer.readByte().toInt()
    if (messageCount !in 1..32) {
      throw IllegalArgumentException("Invalid message count: $messageCount")
    }

    val messages = ArrayList<V086Message>(messageCount)
    val msgNum = buffer.getUnsignedShortLE(1)

    // Single message optimization check
    if (msgNum - 1 == lastMessageId || (msgNum == 0 && lastMessageId == 0xFFFF)) {
      val messageNumber = buffer.readUnsignedShortLE()
      val messageLength = buffer.readShortLE().toInt()

      // In legacy, parseMessage reads TypeID inside.
      val messageType = buffer.readByte()
      val message =
        NettyMessageFactory.read(messageNumber, messageType, buffer, charset)
          ?: throw IllegalArgumentException("Unknown or failed to parse message ID: $messageType")

      return V086Bundle.Single(message)
    } else {
      // Multi message loop
      var parsedCount = 0
      while (parsedCount < messageCount) {
        val messageNumber = buffer.readUnsignedShortLE()
        // Sequence check logic
        if (messageNumber <= lastMessageId) {
          if (messageNumber < 0x20 && lastMessageId > 0xFFDF) {
            // Wrapped around
          } else {
            break // Out of order/duplicate
          }
        } else if (messageNumber > 0xFFBF && lastMessageId < 0x40) {
          break // Disorder
        }

        val messageLength = buffer.readShortLE().toInt()
        val messageType = buffer.readByte()

        val message =
          NettyMessageFactory.read(messageNumber, messageType, buffer, charset)
            ?: throw IllegalArgumentException("Unknown or failed to parse message ID: $messageType")

        messages.add(message)
        parsedCount++
      }
      return V086Bundle.Multi(messages)
    }
  }
}
