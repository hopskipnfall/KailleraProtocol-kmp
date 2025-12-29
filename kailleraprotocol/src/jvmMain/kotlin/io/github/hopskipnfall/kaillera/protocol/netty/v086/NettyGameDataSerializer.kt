package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.GameData
import io.github.hopskipnfall.kaillera.protocol.v086.MessageFormatException
import io.netty.buffer.ByteBuf

object NettyGameDataSerializer : NettyMessageSerializer<GameData> {
  override val messageTypeId: Byte = GameData.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): GameData {
    if (buffer.readableBytes() < 3) { // 1 byte (0x00) + 2 bytes (len)
      // Or simplified, just try to read. Original logic checked readableBytes < 4?
      // V086Message.kt original: if (buffer.readableBytes() < 4) ...
      // but GameData structure is: 0x00, Short(len), Data.
      // 1 + 2 = 3 bytes minimum header.
      // But we can let ByteBuf throw IndexOutOfBoundsException or check strictly.
      // Let's stick to simple logic or mimic V086Message behavior if possible.
    }

    buffer.skipBytes(1) // This is always 0x00.
    val dataSize = buffer.readShortLE().toInt() and 0xFFFF

    if (dataSize <= 0 || dataSize > buffer.readableBytes()) {
      throw MessageFormatException("Invalid Game Data format: dataSize = $dataSize")
    }

    val gameData = ByteArray(dataSize)
    buffer.readBytes(gameData)

    return GameData(messageNumber, gameData)
  }

  override fun write(buffer: ByteBuf, message: GameData, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(message.gameData.size)
    buffer.writeBytes(message.gameData)
  }
}
