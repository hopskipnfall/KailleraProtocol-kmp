package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

sealed class CreateGame : V086Message() {
  override val messageTypeId = ID

  abstract val romName: String

  override val bodyBytes: Int
    get() =
      when (this) {
        is CreateGameRequest ->
          StringUtil.encode(romName, "ISO-8859-1").size +
            1 +
            1 +
            1 +
            2 +
            2 // rom + user(1) + client(1) + gid(2) + val1(2)
        is CreateGameNotification ->
          StringUtil.encode(username, "ISO-8859-1").size +
            1 +
            StringUtil.encode(romName, "ISO-8859-1").size +
            1 +
            StringUtil.encode(clientType, "ISO-8859-1").size +
            1 +
            2 +
            2 // gameId(2) + val1(2)
      }

  override fun writeBodyTo(sink: Sink, charset: String) {
    CreateGameSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x0A
  }

  object CreateGameSerializer : MessageSerializer<CreateGame> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): CreateGame {
      val username = StringUtil.readString(source, charset)
      val romName = StringUtil.readString(source, charset)
      val clientType = StringUtil.readString(source, charset)
      val gameId = source.readShortLe().toInt() and 0xFFFF
      val val1 = source.readShortLe().toInt() and 0xFFFF

      if (username.isEmpty() && clientType.isEmpty()) { // And gameId?
        // Heuristic.
        return CreateGameRequest(messageNumber, romName)
      }
      return CreateGameNotification(messageNumber, username, romName, clientType, gameId, val1)
    }

    override fun write(sink: Sink, message: CreateGame, charset: String) {
      when (message) {
        is CreateGameRequest -> {
          StringUtil.writeString(sink, "", charset) // username
          StringUtil.writeString(sink, message.romName, charset)
          StringUtil.writeString(sink, "", charset) // clientType
          sink.writeShortLe(-1) // 0xFFFF (Short)
          sink.writeShortLe(-1) // 0xFFFF (Short)
        }

        is CreateGameNotification -> {
          StringUtil.writeString(sink, message.username, charset)
          StringUtil.writeString(sink, message.romName, charset)
          StringUtil.writeString(sink, message.clientType, charset)
          sink.writeShortLe(message.gameId.toShort())
          sink.writeShortLe(message.val1.toShort())
        }
      }
    }
  }
}

data class CreateGameNotification(
  override var messageNumber: Int,
  val username: String,
  override val romName: String,
  val clientType: String,
  val gameId: Int,
  val val1: Int,
) : CreateGame(), ServerMessage

data class CreateGameRequest(override var messageNumber: Int, override val romName: String) :
  CreateGame(), ClientMessage
