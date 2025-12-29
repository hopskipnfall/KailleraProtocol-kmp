package io.github.hopskipnfall.kaillera.protocol.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import kotlinx.io.Sink
import kotlinx.io.Source

data class UserInformation(
  override var messageNumber: Int,
  val username: String,
  val clientType: String,
  val connectionType: ConnectionType,
) : V086Message(), ClientMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      StringUtil.encode(username, "ISO-8859-1").size +
        1 +
        StringUtil.encode(clientType, "ISO-8859-1").size +
        1 +
        1

  override fun writeBodyTo(sink: Sink, charset: String) {
    UserInformationSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x03
  }

  object UserInformationSerializer : MessageSerializer<UserInformation> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): UserInformation {
      val username = StringUtil.readString(source, charset)
      val clientType = StringUtil.readString(source, charset)
      val connByte = source.readByte()
      return UserInformation(
        messageNumber,
        username,
        clientType,
        ConnectionType.fromByteValue(connByte),
      )
    }

    override fun write(sink: Sink, message: UserInformation, charset: String) {
      StringUtil.writeString(sink, message.username, charset)
      StringUtil.writeString(sink, message.clientType, charset)
      sink.writeByte(message.connectionType.byteValue)
    }
  }
}
