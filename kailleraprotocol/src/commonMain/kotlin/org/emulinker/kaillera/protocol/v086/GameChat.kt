package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

sealed class GameChat : V086Message() {
  override val messageTypeId = ID
  abstract val message: String

  override val bodyBytes: Int
    get() {
      val name =
        when (this) {
          is GameChatRequest -> ""
          is GameChatNotification -> username
        }
      return StringUtil.encode(name, "ISO-8859-1").size +
        1 +
        StringUtil.encode(message, "ISO-8859-1").size +
        1
    }

  override fun writeBodyTo(sink: Sink, charset: String) {
    GameChatSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x08
  }

  object GameChatSerializer : MessageSerializer<GameChat> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): GameChat {
      val username = StringUtil.readString(source, charset)
      val message = StringUtil.readString(source, charset)

      if (username.isBlank()) {
        return GameChatRequest(messageNumber, message)
      }
      return GameChatNotification(messageNumber, username, message)
    }

    override fun write(sink: Sink, message: GameChat, charset: String) {
      val name =
        when (message) {
          is GameChatRequest -> ""
          is GameChatNotification -> message.username
        }
      StringUtil.writeString(sink, name, charset)
      StringUtil.writeString(sink, message.message, charset)
    }
  }
}

data class GameChatNotification(
  override var messageNumber: Int,
  val username: String,
  override val message: String,
) : GameChat(), ServerMessage

data class GameChatRequest(override var messageNumber: Int, override val message: String) :
  GameChat(), ClientMessage
