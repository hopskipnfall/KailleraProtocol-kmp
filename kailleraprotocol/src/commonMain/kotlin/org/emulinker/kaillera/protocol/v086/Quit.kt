package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

sealed class Quit : V086Message() {
  override val messageTypeId = ID

  abstract val message: String

  override val bodyBytes: Int
    get() =
      when (this) {
        is QuitRequest -> REQUEST_USERNAME
        is QuitNotification -> username
      }.let { StringUtil.encode(it, "ISO-8859-1").size + 1 } +
        2 + // Short (userId)
        StringUtil.encode(message, "ISO-8859-1").size +
        1

  override fun writeBodyTo(sink: Sink, charset: String) {
    QuitSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x01
    const val REQUEST_USERNAME = ""
    const val REQUEST_USER_ID = 0xFFFF
  }

  object QuitSerializer : MessageSerializer<Quit> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): Quit {
      val username = StringUtil.readString(source, charset)
      val userId = source.readShortLe().toInt() and 0xFFFF
      val message = StringUtil.readString(source, charset)

      if (username.isBlank() && userId == REQUEST_USER_ID) {
        return QuitRequest(messageNumber, message)
      }
      return QuitNotification(messageNumber, username, userId, message)
    }

    override fun write(sink: Sink, message: Quit, charset: String) {
      val name =
        when (message) {
          is QuitRequest -> REQUEST_USERNAME
          is QuitNotification -> message.username
        }
      StringUtil.writeString(sink, name, charset)

      val uid =
        when (message) {
          is QuitRequest -> REQUEST_USER_ID
          is QuitNotification -> message.userId
        }
      sink.writeShortLe(uid.toShort())

      StringUtil.writeString(sink, message.message, charset)
    }
  }
}

data class QuitNotification(
  override var messageNumber: Int,
  val username: String,
  val userId: Int,
  override val message: String,
) : Quit(), ServerMessage

data class QuitRequest(override var messageNumber: Int, override val message: String) :
  Quit(), ClientMessage
