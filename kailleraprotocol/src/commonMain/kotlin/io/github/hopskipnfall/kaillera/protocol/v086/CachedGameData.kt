package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

data class CachedGameData(override var messageNumber: Int, val key: Int) :
  V086Message(), ClientMessage, ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int = 2 // 0x00 + Byte(key)

  override fun writeBodyTo(sink: Sink, charset: String) {
    CachedGameDataSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x13
  }

  object CachedGameDataSerializer : MessageSerializer<CachedGameData> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): CachedGameData {
      source.readByte() // 0x00
      val key = source.readByte().toInt() and 0xFF
      return CachedGameData(messageNumber, key)
    }

    override fun write(sink: Sink, message: CachedGameData, charset: String) {
      sink.writeByte(0x00)
      sink.writeByte(message.key.toByte())
    }
  }
}
