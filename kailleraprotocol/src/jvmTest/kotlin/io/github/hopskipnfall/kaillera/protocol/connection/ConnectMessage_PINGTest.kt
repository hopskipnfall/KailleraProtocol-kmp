package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.ConnectMessageTest

class ConnectMessagePingTest : ConnectMessageTest<ConnectMessage_PING>() {
  override val message = ConnectMessage_PING
  override val byteString = "50,49,4E,47,00"
}
