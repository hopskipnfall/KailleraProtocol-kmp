package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.CreateGame

class CreateGameRequestTest : NewV086MessageTest<CreateGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.CreateGameRequest(
      messageNumber = MESSAGE_NUMBER,
      romName = "My Game",
    )
  override val byteString = "00, 4D, 79, 20, 47, 61, 6D, 65, 00, 00, FF, FF, FF, FF"
}

class CreateGameNotificationTest : NewV086MessageTest<CreateGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.CreateGameNotification(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      romName = "My Game",
      clientType = "My N64 Emulator",
      gameId = 100,
      val1 = 4242,
    )
  override val byteString =
    "6E, 75, 65, 00, 4D, 79, 20, 47, 61, 6D, 65, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 64, 00, 92, 10"
}
