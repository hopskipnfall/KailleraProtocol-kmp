package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.AllReady
import io.github.hopskipnfall.kaillera.protocol.v086.CachedGameData
import io.github.hopskipnfall.kaillera.protocol.v086.ClientAck
import io.github.hopskipnfall.kaillera.protocol.v086.CloseGame
import io.github.hopskipnfall.kaillera.protocol.v086.GameKick
import io.github.hopskipnfall.kaillera.protocol.v086.GameStatus
import io.github.hopskipnfall.kaillera.protocol.v086.KeepAlive
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDrop
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropNotification
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropRequest
import io.github.hopskipnfall.kaillera.protocol.v086.Quit
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGame
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.QuitNotification
import io.github.hopskipnfall.kaillera.protocol.v086.QuitRequest
import io.github.hopskipnfall.kaillera.protocol.v086.ServerAck
import io.netty.buffer.ByteBuf
import java.nio.charset.Charset

object NettyKeepAliveSerializer : NettyMessageSerializer<KeepAlive> {
  override val messageTypeId: Byte = KeepAlive.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): KeepAlive {
    val value = buffer.readUnsignedByte().toInt()
    return KeepAlive(messageNumber, value)
  }

  override fun write(buffer: ByteBuf, message: KeepAlive, charset: String) {
    buffer.writeByte(message.value)
  }
}

object NettyQuitSerializer : NettyMessageSerializer<Quit> {
  override val messageTypeId: Byte = Quit.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): Quit {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val userId = buffer.readUnsignedShortLE()
    val message = NettyStringUtil.readString(buffer, 0x00, charsetObj)

    if (username.isEmpty() && userId == 0xFFFF) {
      return QuitRequest(messageNumber, message)
    }
    return QuitNotification(messageNumber, username, userId, message)
  }

  override fun write(buffer: ByteBuf, message: Quit, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is QuitRequest -> ""
        is QuitNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)

    val uid =
      when (message) {
        is QuitRequest -> 0xFFFF
        is QuitNotification -> message.userId
      }
    buffer.writeShortLE(uid)

    NettyStringUtil.writeString(buffer, message.message, 0x00, charsetObj)
  }
}

object NettyAllReadySerializer : NettyMessageSerializer<AllReady> {
  override val messageTypeId: Byte = AllReady.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): AllReady {
    buffer.readByte() // 0x00
    return AllReady(messageNumber)
  }

  override fun write(buffer: ByteBuf, message: AllReady, charset: String) {
    buffer.writeByte(0x00)
  }
}

object NettyCachedGameDataSerializer : NettyMessageSerializer<CachedGameData> {
  override val messageTypeId: Byte = CachedGameData.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): CachedGameData {
    buffer.readByte() // 0x00
    val key = buffer.readUnsignedByte().toInt()
    return CachedGameData(messageNumber, key)
  }

  override fun write(buffer: ByteBuf, message: CachedGameData, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeByte(message.key)
  }
}

object NettyQuitGameSerializer : NettyMessageSerializer<QuitGame> {
  override val messageTypeId: Byte = QuitGame.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): QuitGame {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val userId = buffer.readUnsignedShortLE()

    if (username.isEmpty() && userId == 0xFFFF) {
      return QuitGameRequest(messageNumber)
    }
    return QuitGameNotification(messageNumber, username, userId)
  }

  override fun write(buffer: ByteBuf, message: QuitGame, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is QuitGameRequest -> ""
        is QuitGameNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)

    val uid =
      when (message) {
        is QuitGameRequest -> 0xFFFF
        is QuitGameNotification -> message.userId
      }
    buffer.writeShortLE(uid)
  }
}

object NettyGameKickSerializer : NettyMessageSerializer<GameKick> {
  override val messageTypeId: Byte = GameKick.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): GameKick {
    buffer.readByte() // skip 0x00
    val userId = buffer.readUnsignedShortLE()
    return GameKick(messageNumber, userId)
  }

  override fun write(buffer: ByteBuf, message: GameKick, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(message.userId)
  }
}

object NettyPlayerDropSerializer : NettyMessageSerializer<PlayerDrop> {
  override val messageTypeId: Byte = PlayerDrop.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): PlayerDrop {
    val charsetObj = Charset.forName(charset)
    val username = NettyStringUtil.readString(buffer, 0x00, charsetObj)
    val playerNumber = buffer.readByte().toInt()

    if (username.isEmpty() && playerNumber == 0) {
      return PlayerDropRequest(messageNumber)
    }
    return PlayerDropNotification(messageNumber, username, playerNumber)
  }

  override fun write(buffer: ByteBuf, message: PlayerDrop, charset: String) {
    val charsetObj = Charset.forName(charset)
    val name =
      when (message) {
        is PlayerDropRequest -> ""
        is PlayerDropNotification -> message.username
      }
    NettyStringUtil.writeString(buffer, name, 0x00, charsetObj)

    val num =
      when (message) {
        is PlayerDropRequest -> 0
        is PlayerDropNotification -> message.playerNumber
      }
    buffer.writeByte(num)
  }
}

object NettyClientAckSerializer : NettyMessageSerializer<ClientAck> {
  override val messageTypeId: Byte = ClientAck.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): ClientAck {
    buffer.readByte() // 0x00
    buffer.readIntLE()
    buffer.readIntLE()
    buffer.readIntLE()
    buffer.readIntLE()
    return ClientAck(messageNumber)
  }

  override fun write(buffer: ByteBuf, message: ClientAck, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeIntLE(0)
    buffer.writeIntLE(1)
    buffer.writeIntLE(2)
    buffer.writeIntLE(3)
  }
}

object NettyServerAckSerializer : NettyMessageSerializer<ServerAck> {
  override val messageTypeId: Byte = ServerAck.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): ServerAck {
    buffer.readByte() // 0x00
    val v1 = buffer.readIntLE()
    val v2 = buffer.readIntLE()
    val v3 = buffer.readIntLE()
    val v4 = buffer.readIntLE()
    return ServerAck(messageNumber, v1, v2, v3, v4)
  }

  override fun write(buffer: ByteBuf, message: ServerAck, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeIntLE(message.val1)
    buffer.writeIntLE(message.val2)
    buffer.writeIntLE(message.val3)
    buffer.writeIntLE(message.val4)
  }
}

object NettyCloseGameSerializer : NettyMessageSerializer<CloseGame> {
  override val messageTypeId: Byte = CloseGame.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): CloseGame {
    buffer.readByte() // skip 0x00
    val gameId = buffer.readUnsignedShortLE()
    val val1 = buffer.readUnsignedShortLE()
    return CloseGame(messageNumber, gameId, val1)
  }

  override fun write(buffer: ByteBuf, message: CloseGame, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(message.gameId)
    buffer.writeShortLE(message.val1)
  }
}

object NettyGameStatusSerializer : NettyMessageSerializer<GameStatus> {
  override val messageTypeId: Byte = GameStatus.ID

  override fun read(buffer: ByteBuf, messageNumber: Int, charset: String): GameStatus {
    buffer.readByte() // skip 0x00
    val gameId = buffer.readUnsignedShortLE()
    val val1 = buffer.readUnsignedShortLE()
    val statusByte = buffer.readByte()
    val numPlayers = buffer.readUnsignedByte()
    val maxPlayers = buffer.readUnsignedByte()
    return GameStatus(
      messageNumber,
      gameId,
      val1,
      io.github.hopskipnfall.kaillera.protocol.model.GameStatus.fromByteValue(statusByte),
      numPlayers.toInt(),
      maxPlayers.toInt(),
    )
  }

  override fun write(buffer: ByteBuf, message: GameStatus, charset: String) {
    buffer.writeByte(0x00)
    buffer.writeShortLE(message.gameId)
    buffer.writeShortLE(message.val1)
    buffer.writeByte(message.gameStatus.byteValue.toInt())
    buffer.writeByte(message.numPlayers)
    buffer.writeByte(message.maxPlayers)
  }
}
