package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

sealed class QuitGame : V086Message() {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      when (this) {
        is QuitGameRequest -> REQUEST_USERNAME
        is QuitGameNotification -> username
      }.let { StringUtil.encode(it, "ISO-8859-1").size + 1 } + 2 // Short (userId)

  override fun writeBodyTo(sink: Sink, charset: String) {
    QuitGameSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x0B
    const val REQUEST_USERNAME = ""
    const val REQUEST_USER_ID = 0xFFFF
  }

  object QuitGameSerializer : MessageSerializer<QuitGame> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): QuitGame {
      val username = StringUtil.readString(source, charset)
      val userId = source.readShortLe().toInt() and 0xFFFF

      if (username == REQUEST_USERNAME && userId == REQUEST_USER_ID) {
        return QuitGameRequest(messageNumber)
      }
      return QuitGameNotification(messageNumber, username, userId)
    }

    override fun write(sink: Sink, message: QuitGame, charset: String) {
      val name =
        when (message) {
          is QuitGameRequest -> REQUEST_USERNAME
          is QuitGameNotification -> message.username
        }
      StringUtil.writeString(sink, name, charset)

      val uid =
        when (message) {
          is QuitGameRequest -> REQUEST_USER_ID
          is QuitGameNotification -> message.userId
        }
      sink.writeShortLe(uid.toShort())
    }
  }
}

data class QuitGameNotification(
  override var messageNumber: Int,
  val username: String,
  val userId: Int,
) : QuitGame(), ServerMessage

data class QuitGameRequest(override var messageNumber: Int) : QuitGame(), ClientMessage
