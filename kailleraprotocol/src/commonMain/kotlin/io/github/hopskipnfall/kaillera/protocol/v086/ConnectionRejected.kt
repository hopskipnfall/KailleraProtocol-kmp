package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

data class ConnectionRejected(
  override var messageNumber: Int,
  val username: String,
  val userId: Int,
  val message: String,
) : V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      StringUtil.encode(username, "ISO-8859-1").size +
        1 +
        2 +
        StringUtil.encode(message, "ISO-8859-1").size +
        1

  override fun writeBodyTo(sink: Sink, charset: String) {
    ConnectionRejectedSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x19
  }

  object ConnectionRejectedSerializer : MessageSerializer<ConnectionRejected> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): ConnectionRejected {
      val username = StringUtil.readString(source, charset)
      val userId = source.readShortLe().toInt() and 0xFFFF
      val message = StringUtil.readString(source, charset)
      return ConnectionRejected(messageNumber, username, userId, message)
    }

    override fun write(sink: Sink, message: ConnectionRejected, charset: String) {
      StringUtil.writeString(sink, message.username, charset)
      sink.writeShortLe(message.userId.toShort())
      StringUtil.writeString(sink, message.message, charset)
    }
  }
}
