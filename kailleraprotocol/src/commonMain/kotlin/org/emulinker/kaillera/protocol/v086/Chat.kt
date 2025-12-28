package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

sealed class Chat : V086Message() {
  override val messageTypeId = ID
  abstract val message: String

  override val bodyBytes: Int
    get() {
      val name =
        when (this) {
          is ChatRequest -> ""
          is ChatNotification -> username
        }
      return StringUtil.encode(name, "ISO-8859-1").size +
        1 +
        StringUtil.encode(message, "ISO-8859-1").size +
        1
    }

  override fun writeBodyTo(sink: Sink, charset: String) {
    ChatSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x07
  }

  object ChatSerializer : MessageSerializer<Chat> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): Chat {
      val username = StringUtil.readString(source, charset)
      val message = StringUtil.readString(source, charset)

      if (username.isBlank()) {
        return ChatRequest(messageNumber, message)
      }
      return ChatNotification(messageNumber, username, message)
    }

    override fun write(sink: Sink, message: Chat, charset: String) {
      val name =
        when (message) {
          is ChatRequest -> ""
          is ChatNotification -> message.username
        }
      StringUtil.writeString(sink, name, charset)
      StringUtil.writeString(sink, message.message, charset)
    }
  }
}

data class ChatNotification(
  override var messageNumber: Int,
  val username: String,
  override val message: String,
) : Chat(), ServerMessage

data class ChatRequest(override var messageNumber: Int, override val message: String) :
  Chat(), ClientMessage
