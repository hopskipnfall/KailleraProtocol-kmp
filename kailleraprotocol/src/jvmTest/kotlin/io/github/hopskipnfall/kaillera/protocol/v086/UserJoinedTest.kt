package io.github.hopskipnfall.kaillera.protocol.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import kotlin.time.Duration.Companion.milliseconds

class UserJoinedTest : V086MessageTest<UserJoined>() {
  override val message =
    UserJoined(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      userId = 13,
      ping = 999.milliseconds,
      connectionType = ConnectionType.LAN,
    )
  override val byteString = "6E, 75, 65, 00, 0D, 00, E7, 03, 00, 00, 01"
}
