package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import io.github.hopskipnfall.kaillera.protocol.model.GameStatus
import io.github.hopskipnfall.kaillera.protocol.model.UserStatus
import io.github.hopskipnfall.kaillera.protocol.v086.Chat
import io.github.hopskipnfall.kaillera.protocol.v086.ChatNotification
import io.github.hopskipnfall.kaillera.protocol.v086.ChatRequest
import io.github.hopskipnfall.kaillera.protocol.v086.ConnectionRejected
import io.github.hopskipnfall.kaillera.protocol.v086.CreateGame
import io.github.hopskipnfall.kaillera.protocol.v086.CreateGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.CreateGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.GameChat
import io.github.hopskipnfall.kaillera.protocol.v086.GameChatNotification
import io.github.hopskipnfall.kaillera.protocol.v086.GameChatRequest
import io.github.hopskipnfall.kaillera.protocol.v086.InformationMessage
import io.github.hopskipnfall.kaillera.protocol.v086.JoinGame
import io.github.hopskipnfall.kaillera.protocol.v086.JoinGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.JoinGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerInformation
import io.github.hopskipnfall.kaillera.protocol.v086.ServerStatus
import io.github.hopskipnfall.kaillera.protocol.v086.StartGame
import io.github.hopskipnfall.kaillera.protocol.v086.StartGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.StartGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.UserInformation
import io.github.hopskipnfall.kaillera.protocol.v086.UserJoined
import io.netty.buffer.ByteBuf
import java.nio.charset.Charset
import kotlin.time.Duration.Companion.milliseconds

object NettyChatSerializer : NettyMessageSerializer<Chat> {
  override val messageTypeId: Byte = Chat.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): Chat {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val message = NettyStringUtil.readString(buffer, 0x00, charsetObj)

    if (username.isEmpty()) {
      return ChatRequest(messageNumber, message)
    }
    return ChatNotification(messageNumber, username, message)
  }

  override fun write(buffer: ByteBuf, message: Chat, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is ChatRequest -> ""
        is ChatNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)
    NettyStringUtil.writeString(buffer, message.message, 0x00, charsetObj)
  }
}

object NettyGameChatSerializer : NettyMessageSerializer<GameChat> {
  override val messageTypeId: Byte = GameChat.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): GameChat {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val message = NettyStringUtil.readString(buffer, 0x00, charsetObj)

    if (username.isEmpty()) {
      return GameChatRequest(messageNumber, message)
    }
    return GameChatNotification(messageNumber, username, message)
  }

  override fun write(buffer: ByteBuf, message: GameChat, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is GameChatRequest -> ""
        is GameChatNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)
    NettyStringUtil.writeString(buffer, message.message, 0x00, charsetObj)
  }
}

object NettyCreateGameSerializer : NettyMessageSerializer<CreateGame> {
  override val messageTypeId: Byte = CreateGame.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): CreateGame {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val romName = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val clientType = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val gameId = buffer.readUnsignedShortLE()
    val val1 = buffer.readUnsignedShortLE()

    if (username.isEmpty() && gameId == 0xFFFF && val1 == 0xFFFF) {
      return CreateGameRequest(messageNumber, romName)
    }
    return CreateGameNotification(messageNumber, username, romName, clientType, gameId, val1)
  }

  override fun write(buffer: ByteBuf, message: CreateGame, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is CreateGameRequest -> ""
        is CreateGameNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)
    NettyStringUtil.writeString(buffer, message.romName, 0x00, charsetObj)
    val client =
      when (message) {
        is CreateGameRequest -> ""
        is CreateGameNotification -> message.clientType
      }
    NettyStringUtil.writeString(buffer, client, 0x00, charsetObj)
    val gid =
      when (message) {
        is CreateGameRequest -> 0xFFFF
        is CreateGameNotification -> message.gameId
      }
    buffer.writeShortLE(gid)
    val v1 =
      when (message) {
        is CreateGameRequest -> 0xFFFF
        is CreateGameNotification -> message.val1
      }
    buffer.writeShortLE(v1)
  }
}

object NettyJoinGameSerializer : NettyMessageSerializer<JoinGame> {
  override val messageTypeId: Byte = JoinGame.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): JoinGame {
    buffer.readByte() // skip 0x00
    val gameId = buffer.readUnsignedShortLE()
    val val1 = buffer.readUnsignedShortLE()
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val ping = buffer.readIntLE()
    val userId = buffer.readUnsignedShortLE()
    val connectionType = ConnectionType.fromByteValue(buffer.readByte())

    if (username.isEmpty() && ping == 0 && userId == 0xFFFF) {
      return JoinGameRequest(messageNumber, gameId, connectionType)
    }
    return JoinGameNotification(
      messageNumber,
      gameId,
      val1,
      username,
      ping.milliseconds,
      userId,
      connectionType,
    )
  }

  override fun write(buffer: ByteBuf, message: JoinGame, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(message.gameId)

    when (message) {
      is JoinGameRequest -> {
        buffer.writeShortLE(0) // Val1
        val charsetObj = Charset.forName(charset)
        NettyStringUtil.writeString(buffer, "", 0x00, charsetObj) // Name
        buffer.writeIntLE(0) // Ping
        buffer.writeShortLE(0xFFFF) // UID
        buffer.writeByte(message.connectionType.byteValue.toInt())
      }

      is JoinGameNotification -> {
        buffer.writeShortLE(message.val1)
        val charsetObj = Charset.forName(charset)
        NettyStringUtil.writeString(buffer, message.username, 0x00, charsetObj) // Name
        buffer.writeIntLE(message.ping.inWholeMilliseconds.toInt())
        buffer.writeShortLE(message.userId)
        buffer.writeByte(message.connectionType.byteValue.toInt())
      }
    }
  }
}

object NettyStartGameSerializer : NettyMessageSerializer<StartGame> {
  override val messageTypeId: Byte = StartGame.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): StartGame {
    buffer.readByte() // skip 0x00
    val val1 = buffer.readUnsignedShortLE()
    val playerNumber = buffer.readUnsignedByte()
    val numPlayers = buffer.readUnsignedByte()

    if (val1 == 0xFFFF && playerNumber.toInt() == 0xFF && numPlayers.toInt() == 0xFF) {
      return StartGameRequest(messageNumber)
    }
    return StartGameNotification(messageNumber, numPlayers.toInt(), playerNumber.toInt(), val1)
  }

  override fun write(buffer: ByteBuf, message: StartGame, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(
      when (message) {
        is StartGameRequest -> 0xFFFF
        is StartGameNotification -> message.val1
      }
    )
    buffer.writeByte(
      when (message) {
        is StartGameRequest -> 0xFF
        is StartGameNotification -> message.playerNumber.toInt()
      }
    )
    buffer.writeByte(
      when (message) {
        is StartGameRequest -> 0xFF
        is StartGameNotification -> message.numPlayers.toInt()
      }
    )
  }
}

object NettyUserJoinedSerializer : NettyMessageSerializer<UserJoined> {
  override val messageTypeId: Byte = UserJoined.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): UserJoined {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val userId = buffer.readUnsignedShortLE()
    val ping = buffer.readIntLE()
    val connectionType = ConnectionType.fromByteValue(buffer.readByte())
    return UserJoined(messageNumber, username, userId, ping.milliseconds, connectionType)
  }

  override fun write(buffer: ByteBuf, message: UserJoined, charset: String) {
    val charsetObj = Charset.forName(charset)
    NettyStringUtil.writeString(buffer, message.username, 0x00, charsetObj)
    buffer.writeShortLE(message.userId)
    buffer.writeIntLE(message.ping.inWholeMilliseconds.toInt())
    buffer.writeByte(message.connectionType.byteValue.toInt())
  }
}

object NettyInformationMessageSerializer : NettyMessageSerializer<InformationMessage> {
  override val messageTypeId: Byte = InformationMessage.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): InformationMessage {
    val charsetObj = Charset.forName(charset)
    val source = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val message = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    return InformationMessage(messageNumber, source, message)
  }

  override fun write(buffer: ByteBuf, message: InformationMessage, charset: String) {
    val charsetObj = Charset.forName(charset)
    NettyStringUtil.writeString(buffer, message.source, 0x00, charsetObj)
    NettyStringUtil.writeString(buffer, message.message, 0x00, charsetObj)
  }
}

object NettyConnectionRejectedSerializer : NettyMessageSerializer<ConnectionRejected> {
  override val messageTypeId: Byte = ConnectionRejected.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): ConnectionRejected {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val userId = buffer.readUnsignedShortLE()
    val message = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    return ConnectionRejected(messageNumber, username, userId, message)
  }

  override fun write(buffer: ByteBuf, message: ConnectionRejected, charset: String) {
    val charsetObj = Charset.forName(charset)
    NettyStringUtil.writeString(buffer, message.username, 0x00, charsetObj)
    buffer.writeShortLE(message.userId)
    NettyStringUtil.writeString(buffer, message.message, 0x00, charsetObj)
  }
}

object NettyUserInformationSerializer : NettyMessageSerializer<UserInformation> {
  override val messageTypeId: Byte = UserInformation.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): UserInformation {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val clientType = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val connectionType = ConnectionType.fromByteValue(buffer.readByte())
    return UserInformation(messageNumber, username, clientType, connectionType)
  }

  override fun write(buffer: ByteBuf, message: UserInformation, charset: String) {
    val charsetObj = Charset.forName(charset)
    NettyStringUtil.writeString(buffer, message.username, 0x00, charsetObj)
    NettyStringUtil.writeString(buffer, message.clientType, 0x00, charsetObj)
    buffer.writeByte(message.connectionType.byteValue.toInt())
  }
}

object NettyPlayerInformationSerializer : NettyMessageSerializer<PlayerInformation> {
  override val messageTypeId: Byte = PlayerInformation.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): PlayerInformation {
    buffer.readByte() // skip 0x00
    val numPlayers = buffer.readIntLE()
    val players = ArrayList<PlayerInformation.Player>(numPlayers)
    val charsetObj = Charset.forName(charset)
    for (i in 0 until numPlayers) {
      val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val ping = buffer.readIntLE().milliseconds
      val userId = buffer.readUnsignedShortLE()
      val connectionType = ConnectionType.fromByteValue(buffer.readByte())
      players.add(PlayerInformation.Player(username, ping, userId, connectionType))
    }
    return PlayerInformation(messageNumber, players)
  }

  override fun write(buffer: ByteBuf, message: PlayerInformation, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeIntLE(message.players.size)
    val charsetObj = Charset.forName(charset)
    message.players.forEach {
      NettyStringUtil.writeString(buffer, it.username, 0x00, charsetObj)
      buffer.writeIntLE(it.ping.inWholeMilliseconds.toInt())
      buffer.writeShortLE(it.userId)
      buffer.writeByte(it.connectionType.byteValue.toInt())
    }
  }
}

object NettyServerStatusSerializer : NettyMessageSerializer<ServerStatus> {
  override val messageTypeId: Byte = ServerStatus.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): ServerStatus {
    buffer.readByte() // skip 0x00
    val numUsers = buffer.readIntLE()
    val numGames = buffer.readIntLE()
    val charsetObj = Charset.forName(charset)

    val users = ArrayList<ServerStatus.User>(numUsers)
    for (i in 0 until numUsers) {
      val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val ping = buffer.readIntLE().milliseconds
      val status = UserStatus.fromByteValue(buffer.readByte())
      val userId = buffer.readUnsignedShortLE()
      val connectionType = ConnectionType.fromByteValue(buffer.readByte())
      users.add(ServerStatus.User(username, ping, status, userId, connectionType))
    }

    val games = ArrayList<ServerStatus.Game>(numGames)
    for (i in 0 until numGames) {
      val romName = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val gameId = buffer.readIntLE()
      val clientType = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val playerCountOutOfMax = NettyStringUtil.readString(buffer, 0x00, charsetObj)
      val status = GameStatus.fromByteValue(buffer.readByte())
      games.add(
        ServerStatus.Game(romName, gameId, clientType, username, playerCountOutOfMax, status)
      )
    }

    return ServerStatus(messageNumber, users, games)
  }

  override fun write(buffer: ByteBuf, message: ServerStatus, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeIntLE(message.users.size)
    buffer.writeIntLE(message.games.size)
    val charsetObj = Charset.forName(charset)

    message.users.forEach {
      NettyStringUtil.writeString(buffer, it.username, 0x00, charsetObj)
      buffer.writeIntLE(it.ping.inWholeMilliseconds.toInt())
      buffer.writeByte(it.status.byteValue.toInt())
      buffer.writeShortLE(it.userId)
      buffer.writeByte(it.connectionType.byteValue.toInt())
    }

    message.games.forEach {
      NettyStringUtil.writeString(buffer, it.romName, 0x00, charsetObj)
      buffer.writeIntLE(it.gameId)
      NettyStringUtil.writeString(buffer, it.clientType, 0x00, charsetObj)
      NettyStringUtil.writeString(buffer, it.username, 0x00, charsetObj)
      NettyStringUtil.writeString(buffer, it.playerCountOutOfMax, 0x00, charsetObj)
      buffer.writeByte(it.gameStatus.byteValue.toInt())
    }
  }
}
