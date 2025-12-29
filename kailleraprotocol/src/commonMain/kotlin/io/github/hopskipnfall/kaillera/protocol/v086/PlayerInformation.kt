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

data class PlayerInformation(override var messageNumber: Int, val players: List<Player>) :
  V086Message(), ServerMessage {
  override val messageTypeId = ID

  // Body bytes calculation needed for writeTo
  override val bodyBytes: Int
    get() {
      var size = 1 // 0x00
      size += 4 // count
      for (p in players) {
        size += StringUtil.encode(p.username, "ISO-8859-1").size + 1
        size += 4 + 2 + 1
      }
      return size
    }

  override fun writeBodyTo(sink: Sink, charset: String) {
    PlayerInformationSerializer.write(sink, this, charset)
  }

  data class Player(
    val username: String,
    val ping: Duration,
    val userId: Int,
    val connectionType: ConnectionType,
  )

  companion object {
    const val ID: Byte = 0x0D
  }

  object PlayerInformationSerializer : MessageSerializer<PlayerInformation> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): PlayerInformation {
      source.readByte() // Skip 0x00
      val count = source.readIntLe()
      val players = ArrayList<Player>(count)
      for (i in 0 until count) {
        val username = StringUtil.readString(source, charset)
        val ping = source.readIntLe()
        val userId = source.readShortLe().toInt() and 0xFFFF
        val connByte = source.readByte()
        players.add(
          Player(username, ping.milliseconds, userId, ConnectionType.fromByteValue(connByte))
        )
      }
      return PlayerInformation(messageNumber, players)
    }

    override fun write(sink: Sink, message: PlayerInformation, charset: String) {
      sink.writeByte(0)
      sink.writeIntLe(message.players.size)
      for (p in message.players) {
        StringUtil.writeString(sink, p.username, charset)
        sink.writeIntLe(p.ping.inWholeMilliseconds.toInt())
        sink.writeShortLe(p.userId.toShort())
        sink.writeByte(p.connectionType.byteValue)
      }
    }
  }
}
// I need partial implementation correction for Read.
// I will check original PlayerInformation.kt logic later if tests fail.
// Proceeding with import fix.
