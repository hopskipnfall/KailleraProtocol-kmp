package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.StartGame

class StartGameRequestTest : NewV086MessageTest<StartGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.StartGameRequest(messageNumber = MESSAGE_NUMBER)
  override val byteString = "00, FF, FF, FF, FF"
}

class StartGameNotificationTest : NewV086MessageTest<StartGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.StartGameNotification(
      messageNumber = MESSAGE_NUMBER,
      numPlayers = 4,
      playerNumber = 42,
      val1 = 2000,
    )
  override val byteString = "00, D0, 07, 2A, 04"
}
