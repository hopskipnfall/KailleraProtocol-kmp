package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import io.github.hopskipnfall.kaillera.protocol.v086.JoinGame
import kotlin.time.Duration.Companion.milliseconds

class JoinGameRequestTest : NewV086MessageTest<JoinGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.JoinGameRequest(
      messageNumber = MESSAGE_NUMBER,
      gameId = 135,
      connectionType = ConnectionType.BAD,
    )
  override val byteString = "00, 87, 00, 00, 00, 00, 00, 00, 00, 00, FF, FF, 06"
}

class JoinGameNotificationTest : NewV086MessageTest<JoinGame>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.JoinGameNotification(
      messageNumber = MESSAGE_NUMBER,
      gameId = 135,
      val1 = 1234,
      username = "nue",
      ping = 1235.milliseconds,
      userId = 13,
      connectionType = ConnectionType.BAD,
    )
  override val byteString = "00, 87, 00, D2, 04, 6E, 75, 65, 00, D3, 04, 00, 00, 0D, 00, 06"
}
