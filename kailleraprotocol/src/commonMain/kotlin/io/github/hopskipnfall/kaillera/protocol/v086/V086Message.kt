package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readShortLe
import kotlinx.io.writeShortLe

open class MessageFormatException(message: String) : Exception(message)

abstract class V086Message {
  abstract var messageNumber: Int
  abstract val messageTypeId: Byte
  abstract val bodyBytes: Int

  val bodyBytesPlusMessageIdType: Int
    get() = bodyBytes + 1

  /**
   * Writes the complete message to the [sink], including the header (message number, length,
   * message type ID) and the body.
   *
   * The structure sent is:
   * - Message Number (2 bytes, LE)
   * - Length (2 bytes, LE): Body Bytes + 1 (for Message Type ID)
   * - Message Type ID (1 byte)
   * - Body (Variable)
   */
  @Throws(MessageFormatException::class)
  fun writeTo(sink: Sink, charset: String) {
    sink.writeShortLe(messageNumber.toShort())
    sink.writeShortLe(bodyBytesPlusMessageIdType.toShort())
    sink.writeByte(messageTypeId)
    writeBodyTo(sink, charset)
  }

  /**
   * Writes only the message body (payload) to the [sink]. This should NOT include the standard
   * message header (message number, length, message type ID), as that is handled by [writeTo].
   */
  abstract fun writeBodyTo(sink: Sink, charset: String)

  companion object {
    fun parse(source: Source, charset: String): V086Message {
      val messageNumber = source.readShortLe().toInt() and 0xFFFF
      val length = source.readShortLe().toInt() and 0xFFFF
      val messageTypeId = source.readByte()

      return when (messageTypeId) {
        Quit.ID -> Quit.QuitSerializer.read(source, messageNumber, charset)
        JoinGame.ID -> JoinGame.JoinGameSerializer.read(source, messageNumber, charset)
        QuitGame.ID -> QuitGame.QuitGameSerializer.read(source, messageNumber, charset)
        GameChat.ID -> GameChat.GameChatSerializer.read(source, messageNumber, charset)
        KeepAlive.ID -> KeepAlive.KeepAliveSerializer.read(source, messageNumber, charset)
        ClientAck.ID -> ClientAck.ClientAckSerializer.read(source, messageNumber, charset)
        ServerAck.ID -> ServerAck.ServerAckSerializer.read(source, messageNumber, charset)
        Chat.ID -> Chat.ChatSerializer.read(source, messageNumber, charset)
        PlayerDrop.ID -> PlayerDrop.PlayerDropSerializer.read(source, messageNumber, charset)
        CreateGame.ID -> CreateGame.CreateGameSerializer.read(source, messageNumber, charset)
        UserJoined.ID -> UserJoined.UserJoinedSerializer.read(source, messageNumber, charset)
        ConnectionRejected.ID ->
          ConnectionRejected.ConnectionRejectedSerializer.read(source, messageNumber, charset)

        StartGame.ID -> StartGame.StartGameSerializer.read(source, messageNumber, charset)
        AllReady.ID -> AllReady.AllReadySerializer.read(source, messageNumber, charset)
        GameData.ID -> GameData.GameDataSerializer.read(source, messageNumber, charset)
        CachedGameData.ID ->
          CachedGameData.CachedGameDataSerializer.read(source, messageNumber, charset)

        UserInformation.ID ->
          UserInformation.UserInformationSerializer.read(source, messageNumber, charset)

        GameStatus.ID -> GameStatus.GameStatusSerializer.read(source, messageNumber, charset)
        InformationMessage.ID ->
          InformationMessage.InformationMessageSerializer.read(source, messageNumber, charset)

        PlayerInformation.ID ->
          PlayerInformation.PlayerInformationSerializer.read(source, messageNumber, charset)

        ServerStatus.ID -> ServerStatus.ServerStatusSerializer.read(source, messageNumber, charset)
        CloseGame.ID -> CloseGame.CloseGameSerializer.read(source, messageNumber, charset)
        GameKick.ID -> GameKick.GameKickSerializer.read(source, messageNumber, charset)
        else -> throw MessageFormatException("Unknown message type ID: $messageTypeId")
      }
    }
  }
}

sealed interface MessageSerializer<T : V086Message> {
  val messageTypeId: Byte

  fun read(source: Source, messageNumber: Int, charset: String): T

  fun write(sink: Sink, message: T, charset: String)
}
