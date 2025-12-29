package io.github.hopskipnfall.kaillera.protocol.v086

class QuitGameRequestTest : V086MessageTest<QuitGame>() {
  override val message = QuitGameRequest(messageNumber = MESSAGE_NUMBER)
  override val byteString = "00, FF, FF"
}

class QuitGameNotificationTest : V086MessageTest<QuitGame>() {
  override val message =
    QuitGameNotification(messageNumber = MESSAGE_NUMBER, username = "nue", userId = 13)
  override val byteString = "6E, 75, 65, 00, 0D, 00"
}
