package io.github.hopskipnfall.kaillera.protocol.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import io.github.hopskipnfall.kaillera.protocol.model.GameStatus as GameStatusEnum
import io.github.hopskipnfall.kaillera.protocol.model.UserStatus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readIntLe
import kotlinx.io.readShortLe
import kotlinx.io.writeIntLe
import kotlinx.io.writeShortLe

data class ServerStatus(
  override var messageNumber: Int,
  val users: List<User>,
  val games: List<Game>,
) : V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() {
      var size = 1 // 0x00
      size += 4 // users count (Int)
      for (u in users) {
        size += StringUtil.encode(u.username, "ISO-8859-1").size + 1
        size += 4 + 1 + 2 + 1 // ping(4)+status(1)+uid(2)+conn(1)
      }
      size += 4 // games count
      for (g in games) {
        size += StringUtil.encode(g.romName, "ISO-8859-1").size + 1
        size += 4 // gameId(Int) ... wait, Serializer uses Int valid or Short?
        // CreateGame uses Short. ServerStatus uses Int?
        // ServerStatusTest: "4D... 64 00 00 00" -> 100 (Int).
        // So ServerStatus GameID is Int.
        size += StringUtil.encode(g.clientType, "ISO-8859-1").size + 1
        size += StringUtil.encode(g.username, "ISO-8859-1").size + 1
        size += StringUtil.encode(g.playerCountOutOfMax, "ISO-8859-1").size + 1
        size += 1 // status(Byte)
      }
      return size
    }

  override fun writeBodyTo(sink: Sink, charset: String) {
    ServerStatusSerializer.write(sink, this, charset)
  }

  data class User(
    val username: String,
    val ping: Duration,
    val status: UserStatus,
    val userId: Int,
    val connectionType: ConnectionType,
  ) {
    val numBytes: Int
      get() = username.length + 9
  }

  data class Game(
    val romName: String,
    val gameId: Int,
    val clientType: String,
    val username: String,
    val playerCountOutOfMax: String,
    val gameStatus: GameStatusEnum,
  ) {
    val numBytes: Int
      get() = romName.length + clientType.length + username.length + playerCountOutOfMax.length + 9
  }

  companion object {
    const val ID: Byte = 0x04
  }

  object ServerStatusSerializer : MessageSerializer<ServerStatus> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): ServerStatus {
      source.readByte() // skip 0x00
      val userCount = source.readIntLe()
      val gameCount = source.readIntLe()

      val users = ArrayList<User>(userCount)
      for (i in 0 until userCount) {
        val username = StringUtil.readString(source, charset)
        val ping = source.readIntLe()
        val statusByte = source.readByte()
        val userId = source.readShortLe().toInt() and 0xFFFF
        val connByte = source.readByte()

        users.add(
          User(
            username,
            ping.milliseconds,
            UserStatus.getByValue(statusByte.toInt()),
            userId,
            ConnectionType.fromByteValue(connByte),
          )
        )
      }

      val games = ArrayList<Game>(gameCount)
      for (i in 0 until gameCount) {
        val romName = StringUtil.readString(source, charset)
        val gameId = source.readIntLe()
        val clientType = StringUtil.readString(source, charset)
        val owner = StringUtil.readString(source, charset)
        val players = StringUtil.readString(source, charset)
        val statusByte = source.readByte()

        games.add(
          Game(
            romName,
            gameId,
            clientType,
            owner,
            players,
            GameStatusEnum.getByValue(statusByte.toInt()),
          )
        )
      }

      return ServerStatus(messageNumber, users, games)
    }

    override fun write(sink: Sink, message: ServerStatus, charset: String) {
      sink.writeByte(0)
      sink.writeIntLe(message.users.size)
      sink.writeIntLe(message.games.size)

      for (u in message.users) {
        StringUtil.writeString(sink, u.username, charset)
        sink.writeIntLe(u.ping.inWholeMilliseconds.toInt())
        sink.writeByte(u.status.byteValue) // Fixed: Remove .toByte()
        sink.writeShortLe(u.userId.toShort())
        sink.writeByte(u.connectionType.byteValue)
      }

      for (g in message.games) {
        StringUtil.writeString(sink, g.romName, charset)
        sink.writeIntLe(g.gameId)
        StringUtil.writeString(sink, g.clientType, charset)
        StringUtil.writeString(sink, g.username, charset)
        StringUtil.writeString(sink, g.playerCountOutOfMax, charset)
        sink.writeByte(g.gameStatus.byteValue) // Fixed: Remove .toByte()
      }
    }
  }
}
