package org.emulinker.kaillera.protocol.v086

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readIntLe
import kotlinx.io.readShortLe
import kotlinx.io.writeIntLe
import kotlinx.io.writeShortLe
import org.emulinker.kaillera.model.ConnectionType

data class UserJoined(
  override var messageNumber: Int,
  val username: String,
  val userId: Int,
  val ping: Duration,
  val connectionType: ConnectionType,
) : V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      StringUtil.encode(username, "ISO-8859-1").size +
        1 +
        2 +
        4 +
        1 // name + null + short + int + byte

  override fun writeBodyTo(sink: Sink, charset: String) {
    UserJoinedSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x02
  }

  object UserJoinedSerializer : MessageSerializer<UserJoined> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): UserJoined {
      val username = StringUtil.readString(source, charset)
      val userId = source.readShortLe().toInt() and 0xFFFF
      val pingVal = source.readIntLe()
      val connectionTypeVal = source.readByte()

      return UserJoined(
        messageNumber,
        username,
        userId,
        pingVal.milliseconds,
        ConnectionType.fromByteValue(connectionTypeVal),
      )
    }

    override fun write(sink: Sink, message: UserJoined, charset: String) {
      StringUtil.writeString(sink, message.username, charset)
      sink.writeShortLe(message.userId.toShort())
      sink.writeIntLe(message.ping.inWholeMilliseconds.toInt())
      sink.writeByte(message.connectionType.byteValue)
    }
  }
}
