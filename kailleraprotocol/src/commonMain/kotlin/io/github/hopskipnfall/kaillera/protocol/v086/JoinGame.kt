package io.github.hopskipnfall.kaillera.protocol.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readIntLe
import kotlinx.io.readShortLe
import kotlinx.io.writeIntLe
import kotlinx.io.writeShortLe

sealed class JoinGame : V086Message() {
  override val messageTypeId = ID

  abstract val gameId: Int
  abstract val connectionType: ConnectionType

  override val bodyBytes: Int
    get() =
      when (this) {
        is JoinGameRequest -> 1 + 2 + 2 + 1 + 4 + 2 + 1 // 13 bytes
        is JoinGameNotification ->
          1 + 2 + 2 + StringUtil.encode(username, "ISO-8859-1").size + 1 + 4 + 2 + 1
      }

  override fun writeBodyTo(sink: Sink, charset: String) {
    JoinGameSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x0C
  }

  object JoinGameSerializer : MessageSerializer<JoinGame> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): JoinGame {
      source.readByte() // 0x00 padding byte
      val gameId = source.readShortLe().toInt() and 0xFFFF
      val val1 = source.readShortLe().toInt() and 0xFFFF
      val username = StringUtil.readString(source, charset)
      val ping = source.readIntLe()
      val userId = source.readShortLe().toInt() and 0xFFFF
      val connType = source.readByte()

      if (username.isEmpty() && ping == 0 && userId == 0xFFFF) {
        return JoinGameRequest(messageNumber, gameId, ConnectionType.fromByteValue(connType))
      }

      return JoinGameNotification(
        messageNumber,
        gameId,
        val1,
        username,
        ping.milliseconds,
        userId,
        ConnectionType.fromByteValue(connType),
      )
    }

    override fun write(sink: Sink, message: JoinGame, charset: String) {
      sink.writeByte(0x00) // Padding byte
      when (message) {
        is JoinGameRequest -> {
          sink.writeShortLe(message.gameId.toShort())
          sink.writeShortLe(0) // Val1
          StringUtil.writeString(sink, "", charset) // Name
          sink.writeIntLe(0) // Ping
          sink.writeShortLe(-1) // 0xFFFF
          sink.writeByte(message.connectionType.byteValue)
        }

        is JoinGameNotification -> {
          sink.writeShortLe(message.gameId.toShort())
          sink.writeShortLe(message.val1.toShort())
          StringUtil.writeString(sink, message.username, charset)
          sink.writeIntLe(message.ping.inWholeMilliseconds.toInt())
          sink.writeShortLe(message.userId.toShort())
          sink.writeByte(message.connectionType.byteValue)
        }
      }
    }
  }
}

data class JoinGameNotification(
  override var messageNumber: Int,
  override val gameId: Int,
  val val1: Int,
  val username: String,
  val ping: Duration,
  val userId: Int,
  override val connectionType: ConnectionType,
) : JoinGame(), ServerMessage

data class JoinGameRequest(
  override var messageNumber: Int,
  override val gameId: Int,
  override val connectionType: ConnectionType,
) : JoinGame(), ClientMessage
