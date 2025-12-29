package io.github.hopskipnfall.kaillera.protocol.v086

import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType

class UserInformationTest : V086MessageTest<UserInformation>() {
  override val message =
    UserInformation(
      messageNumber = MESSAGE_NUMBER,
      username = "nue",
      clientType = "My Emulator",
      connectionType = ConnectionType.LAN,
    )
  override val byteString = "6E, 75, 65, 00, 4D, 79, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 01"
}
