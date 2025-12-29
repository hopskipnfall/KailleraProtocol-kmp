package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Source

object MessageFactory {
  internal val serializers: Array<MessageSerializer<*>?> =
    arrayOf(
      null, // 0x00
      Quit.QuitSerializer, // 0x01
      UserJoined.UserJoinedSerializer, // 0x02
      UserInformation.UserInformationSerializer, // 0x03
      ServerStatus.ServerStatusSerializer, // 0x04
      ServerAck.ServerAckSerializer, // 0x05
      ClientAck.ClientAckSerializer, // 0x06
      Chat.ChatSerializer, // 0x07
      GameChat.GameChatSerializer, // 0x08
      KeepAlive.KeepAliveSerializer, // 0x09
      CreateGame.CreateGameSerializer, // 0x0A
      QuitGame.QuitGameSerializer, // 0x0B
      JoinGame.JoinGameSerializer, // 0x0C
      PlayerInformation.PlayerInformationSerializer, // 0x0D
      GameStatus.GameStatusSerializer, // 0x0E
      GameKick.GameKickSerializer, // 0x0F
      CloseGame.CloseGameSerializer, // 0x10
      StartGame.StartGameSerializer, // 0x11
      GameData.GameDataSerializer, // 0x12
      CachedGameData.CachedGameDataSerializer, // 0x13
      PlayerDrop.PlayerDropSerializer, // 0x14
      AllReady.AllReadySerializer, // 0x15
      null, // 0x16
      InformationMessage.InformationMessageSerializer, // 0x17
      null, // 0x18
      ConnectionRejected.ConnectionRejectedSerializer, // 0x19
    )

  fun read(source: Source, messageNumber: Int, messageTypeId: Byte, charset: String): V086Message? {
    val id = messageTypeId.toInt()
    if (id < 0 || id >= serializers.size) return null
    val serializer = serializers[id]
    return serializer?.read(source, messageNumber, charset)
  }
}
