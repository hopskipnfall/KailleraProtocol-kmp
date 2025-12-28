package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

sealed class PlayerDrop : V086Message() {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      when (this) {
        is PlayerDropRequest -> REQUEST_USERNAME
        is PlayerDropNotification -> username
      }.let { StringUtil.encode(it, "ISO-8859-1").size + 1 } + 1 // Byte (playerNumber)

  override fun writeBodyTo(sink: Sink, charset: String) {
    PlayerDropSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x14
    const val REQUEST_USERNAME = ""
    const val REQUEST_PLAYER_NUMBER = 0
  }

  object PlayerDropSerializer : MessageSerializer<PlayerDrop> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): PlayerDrop {
      val username = StringUtil.readString(source, charset)
      val playerNumber = source.readByte().toInt() and 0xFF

      if (username == REQUEST_USERNAME && playerNumber == REQUEST_PLAYER_NUMBER) {
        return PlayerDropRequest(messageNumber)
      }
      return PlayerDropNotification(messageNumber, username, playerNumber)
    }

    override fun write(sink: Sink, message: PlayerDrop, charset: String) {
      val name =
        when (message) {
          is PlayerDropRequest -> REQUEST_USERNAME
          is PlayerDropNotification -> message.username
        }
      StringUtil.writeString(sink, name, charset)

      val num =
        when (message) {
          is PlayerDropRequest -> REQUEST_PLAYER_NUMBER
          is PlayerDropNotification -> message.playerNumber
        }
      sink.writeByte(num.toByte())
    }
  }
}

data class PlayerDropNotification(
  override var messageNumber: Int,
  val username: String,
  val playerNumber: Int,
) : PlayerDrop(), ServerMessage

data class PlayerDropRequest(override var messageNumber: Int) : PlayerDrop(), ClientMessage
