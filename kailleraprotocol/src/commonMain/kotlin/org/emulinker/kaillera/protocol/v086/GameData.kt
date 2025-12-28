package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

/**
 * A message sent by both the client and server, which contains game input data as a [ByteArray].
 *
 * Message type ID: `0x12`.
 */
data class GameData(override var messageNumber: Int, val gameData: ByteArray) : V086Message() {
  override val messageTypeId: Byte = ID

  override val bodyBytes: Int
    get() = 1 + 2 + gameData.size // 0x00 + dataSize(Short) + data

  override fun writeBodyTo(sink: Sink, charset: String) {
    GameDataSerializer.write(sink, this, charset)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || this::class != other::class) return false

    other as GameData

    if (messageNumber != other.messageNumber) return false
    if (!gameData.contentEquals(other.gameData)) return false
    if (messageTypeId != other.messageTypeId) return false

    return true
  }

  override fun hashCode(): Int {
    var result = messageNumber
    result = 31 * result + gameData.contentHashCode()
    result = 31 * result + messageTypeId
    return result
  }

  init {
    require(gameData.isNotEmpty()) { "gameData is empty" }
    require(gameData.size in 0..0xFFFF) { "gameData size out of range: ${gameData.size}" }
  }

  companion object {
    const val ID: Byte = 0x12
  }

  object GameDataSerializer : MessageSerializer<GameData> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): GameData {
      // Original code checked readableBytes < 4, but Source doesn't easily expose total remaining
      // length upfront cleanly without request.
      // However based on protocol logic, we read field by field.

      // buffer.skipBytes(1) // This is always 0x00.
      // In original code: Read 1 byte (0x00), Read Short (length), Read Bytes.

      source.readByte() // Skip 0x00

      val dataSize = source.readShortLe().toInt() and 0xFFFF
      if (
        dataSize <= 0
      ) { //  || dataSize > buffer.readableBytes() -> Can't easily check total bounds on infinite
        // source, but we will fail on readPacket if not enough.
        throw MessageFormatException("Invalid Game Data format: dataSize = $dataSize")
      }

      val gameData = source.readByteArray(dataSize)
      return GameData(messageNumber, gameData)
    }

    override fun write(sink: Sink, message: GameData, charset: String) {
      sink.writeByte(0x00)
      sink.writeShortLe(message.gameData.size.toShort())
      sink.write(message.gameData)
    }
  }
}
