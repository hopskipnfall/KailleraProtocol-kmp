package io.github.hopskipnfall.kaillera.protocol.v086

class StartGameRequestTest : V086MessageTest<StartGame>() {
  override val message = StartGameRequest(messageNumber = MESSAGE_NUMBER)
  override val byteString = "00, FF, FF, FF, FF"
}

class StartGameNotificationTest : V086MessageTest<StartGame>() {
  override val message =
    StartGameNotification(
      messageNumber = MESSAGE_NUMBER,
      numPlayers = 4,
      playerNumber = 42,
      val1 = 2000,
    )
  override val byteString = "00, D0, 07, 2A, 04"
}
