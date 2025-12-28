package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe
import org.emulinker.kaillera.model.GameStatus as GameStatusEnum

data class GameStatus(
  override var messageNumber: Int,
  val gameId: Int,
  val val1: Int,
  val gameStatus: GameStatusEnum,
  val numPlayers: Int,
  val maxPlayers: Int,
) : V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int = 8 // 0x00 + gameId(2) + val1(2) + status(1) + num(1) + max(1)

  override fun writeBodyTo(sink: Sink, charset: String) {
    GameStatusSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x0E
  }

  object GameStatusSerializer : MessageSerializer<GameStatus> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): GameStatus {
      source.readByte() // skip 0x00
      val gameId = source.readShortLe().toInt() and 0xFFFF
      val val1 = source.readShortLe().toInt() and 0xFFFF
      val statusByte = source.readByte()
      val numPlayers = source.readByte().toInt()
      val maxPlayers = source.readByte().toInt()
      return GameStatus(
        messageNumber,
        gameId,
        val1,
        GameStatusEnum.fromByteValue(statusByte),
        numPlayers,
        maxPlayers,
      )
    }

    override fun write(sink: Sink, message: GameStatus, charset: String) {
      sink.writeByte(0)
      sink.writeShortLe(message.gameId.toShort())
      sink.writeShortLe(message.val1.toShort())
      sink.writeByte(message.gameStatus.byteValue)
      sink.writeByte(message.numPlayers.toByte())
      sink.writeByte(message.maxPlayers.toByte())
    }
  }
}
