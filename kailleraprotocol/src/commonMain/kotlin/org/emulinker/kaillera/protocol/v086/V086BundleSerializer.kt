package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

object V086BundleSerializer {
  fun read(source: Source, lastMessageId: Int = -1, charset: String): V086Bundle {
    val messageCount = source.readByte().toInt()

    // Peek or read ahead to check for the Single optimization (seq=last+1 or 0/FFFF exception)
    // Since Source doesn't support random access easily like ByteBuf, we might have to read
    // sequentially.
    // However, the bundle format is:
    // Byte: Count
    // Loop:
    //   Short: MessageNumber
    //   Short: Length
    //   Byte: Type (inside body)
    //   Body...

    // If messageCount is unreasonable, we might fail.

    val messages = ArrayList<V086Message>(messageCount)
    var parsedCount = 0

    // Logic adapted from legacy V086Bundle.parse

    // To handle the "Single" optimization where we might return early, we check the first message
    // number.
    // KMP Source doesn't have peekShortLE easily without buffering.
    // But we are committed to reading the bundle anyway.

    while (parsedCount < messageCount) {
      // We need to peek specifically for the logic:
      // if (msgNum - 1 == lastMessageID || msgNum == 0 && lastMessageID == 0xFFFF)
      // But we can just iterate.

      if (source.exhausted()) break

      val messageNumber = source.readShortLe().toInt() and 0xFFFF
      // Sequence checks
      if (messageNumber <= lastMessageId) {
        if (messageNumber < 0x20 && lastMessageId > 0xFFDF) {
          // Wrap-around exception, continue
        } else {
          // Out of order or duplicate, stop parsing?
          // Legacy code 'break's here for Multi, effectively discarding the rest?
          // But for Single optimization it's different.
        }
      }

      val messageLength = source.readShortLe().toInt() and 0xFFFF

      // Create a limited source for the message body to ensure we don't over-read
      // verify messageLength

      val messageTypeId = source.readByte()

      // We need a way to limit reading to (messageLength - 1) bytes for the body.
      // kotlinx-io doesn't have a direct "view" or "slice" that returns a Source easily without
      // reading into buffer.
      // But MessageFactory.read takes a Source.
      // We can use `source.peek().readByteArray(length)`? No.

      // Best approach with kotlinx-io:
      // Read the body into a buffer, then wrap that buffer as a Source.
      // bodyBytes = messageLength - 1 (typeId is 1 byte, already read)

      val bodySize = messageLength - 1
      val bodyBuffer = Buffer()
      source.readTo(bodyBuffer, bodySize.toLong())

      val message = MessageFactory.read(bodyBuffer, messageNumber, messageTypeId, charset)

      if (message != null) {
        messages.add(message)
      }

      parsedCount++
    }

    if (messages.size == 1) {
      return V086Bundle.Single(messages[0])
    }
    return V086Bundle.Multi(messages)
  }

  fun write(sink: Sink, bundle: V086Bundle, charset: String) {
    val msgs = bundle.messages
    sink.writeByte(msgs.size.toByte())
    for (msg in msgs) {
      sink.writeShortLe(msg.messageNumber.toShort())
      // Calculate length: bodyBytes + 1 (for typeId)
      val len = msg.bodyBytes + 1
      sink.writeShortLe(len.toShort())
      sink.writeByte(msg.messageTypeId)
      msg.writeBodyTo(sink, charset)
    }
  }
}
