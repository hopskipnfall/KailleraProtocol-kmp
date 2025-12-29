package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.ConnectMessageTest

class ConnectMessageTooTest : ConnectMessageTest<ConnectMessage_ServerFull>() {
  override val message = ConnectMessage_ServerFull
  override val byteString = "54,4F,4F,00"
}
