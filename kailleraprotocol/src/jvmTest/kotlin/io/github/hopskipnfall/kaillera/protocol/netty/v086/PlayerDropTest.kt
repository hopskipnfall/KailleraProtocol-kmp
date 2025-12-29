package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDrop

class PlayerDropRequestTest : NewV086MessageTest<PlayerDrop>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropRequest(messageNumber = MESSAGE_NUMBER)
  override val byteString = "00, 00"
}

class PlayerDropNotificationTest : NewV086MessageTest<PlayerDrop>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropNotification(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      playerNumber = 100,
    )
  override val byteString = "6E, 75, 65, 00, 64"
}
