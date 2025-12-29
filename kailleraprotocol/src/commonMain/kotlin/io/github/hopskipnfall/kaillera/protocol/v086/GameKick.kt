package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

data class GameKick(override var messageNumber: Int, val userId: Int) :
  V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int = 3 // 0x00 + Short

  override fun writeBodyTo(sink: Sink, charset: String) {
    GameKickSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x0F
  }

  object GameKickSerializer : MessageSerializer<GameKick> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): GameKick {
      source.readByte() // 0x00
      val userId = source.readShortLe().toInt() and 0xFFFF
      return GameKick(messageNumber, userId)
    }

    override fun write(sink: Sink, message: GameKick, charset: String) {
      sink.writeByte(0)
      sink.writeShortLe(message.userId.toShort())
    }
  }
}
