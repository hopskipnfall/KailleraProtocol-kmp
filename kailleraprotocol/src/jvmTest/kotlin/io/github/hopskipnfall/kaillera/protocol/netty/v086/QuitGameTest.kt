package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.QuitGame

class QuitGameRequestTest : NewV086MessageTest<QuitGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.QuitGameRequest(messageNumber = MESSAGE_NUMBER)
  override val byteString = "00, FF, FF"
}

class QuitGameNotificationTest : NewV086MessageTest<QuitGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.QuitGameNotification(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      userId = 13,
    )
  override val byteString = "6E, 75, 65, 00, 0D, 00"
}
