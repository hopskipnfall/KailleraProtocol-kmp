package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.GameChat

class GameChatRequestTest : NewV086MessageTest<GameChat>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.GameChatRequest(
      messageNumber = MESSAGE_NUMBER,
      message = "Hello, world!",
    )
  override val byteString = "00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
}

class GameChatNotificationTest : NewV086MessageTest<GameChat>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.GameChatNotification(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      message = "Hello, world!",
    )
  override val byteString = "6E, 75, 65, 00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
}
