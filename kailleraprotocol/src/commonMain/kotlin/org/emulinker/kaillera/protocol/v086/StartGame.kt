package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

sealed class StartGame : V086Message() {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      when (this) {
        is StartGameRequest -> 1 + 2 + 2 // 0x00 + 0xFFFF + 0xFFFF
        is StartGameNotification -> 1 + 2 + 1 + 1 // 0x00 + val1(S) + num(B) + player(B)
      }

  override fun writeBodyTo(sink: Sink, charset: String) {
    StartGameSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x11
  }

  object StartGameSerializer : MessageSerializer<StartGame> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): StartGame {
      source.readByte() // Skip 0x00

      val v1 = source.readShortLe().toInt() and 0xFFFF
      val v2 = source.readShortLe().toInt() and 0xFFFF

      if (v1 == 0xFFFF && v2 == 0xFFFF) {
        return StartGameRequest(messageNumber)
      }

      // Notification: v1 is val1 (Short). v2 is (playerNumber << 8) | numPlayers ?
      // Original StartGame.java:
      // val1 = buffer.readUnsignedShortLE()
      // playerNumber = buffer.readUnsignedByte()
      // numPlayers = buffer.getUnsignedByte()
      // Total 2 + 1 + 1 = 4 bytes remaining.
      // Helper v2 read 2 bytes.
      // v2 LE Short. Low byte = first byte = playerNumber.
      // High byte = second byte = numPlayers.

      val playerNumber = v2 and 0xFF
      val numPlayers = (v2 shr 8) and 0xFF

      return StartGameNotification(messageNumber, numPlayers, playerNumber, v1)
    }

    override fun write(sink: Sink, message: StartGame, charset: String) {
      sink.writeByte(0x00)
      when (message) {
        is StartGameRequest -> {
          sink.writeShortLe(-1) // 0xFFFF
          sink.writeShortLe(-1) // 0xFFFF
        }

        is StartGameNotification -> {
          sink.writeShortLe(message.val1.toShort())
          sink.writeByte(message.playerNumber.toByte())
          sink.writeByte(message.numPlayers.toByte())
        }
      }
    }
  }
}

data class StartGameNotification(
  override var messageNumber: Int,
  val numPlayers: Int,
  val playerNumber: Int,
  val val1: Int,
) : StartGame(), ServerMessage

data class StartGameRequest(override var messageNumber: Int) : StartGame(), ClientMessage
