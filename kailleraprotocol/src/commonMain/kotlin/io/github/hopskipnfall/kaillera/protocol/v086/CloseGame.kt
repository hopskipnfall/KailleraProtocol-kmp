package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

data class CloseGame(override var messageNumber: Int, val gameId: Int, val val1: Int) :
  V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int = 5 // 0x00 + Short + Short

  override fun writeBodyTo(sink: Sink, charset: String) {
    CloseGameSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x10
  }

  object CloseGameSerializer : MessageSerializer<CloseGame> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): CloseGame {
      source.readByte() // 0x00
      val gameId = source.readShortLe().toInt() and 0xFFFF
      val val1 = source.readShortLe().toInt() and 0xFFFF
      return CloseGame(messageNumber, gameId, val1)
    }

    override fun write(sink: Sink, message: CloseGame, charset: String) {
      sink.writeByte(0x00)
      sink.writeShortLe(message.gameId.toShort())
      sink.writeShortLe(message.val1.toShort())
    }
  }
}
