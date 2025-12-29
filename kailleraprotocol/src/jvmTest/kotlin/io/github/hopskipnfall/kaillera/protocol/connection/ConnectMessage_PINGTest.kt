package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NewConnectMessageTest

class ConnectMessagePingTest : NewConnectMessageTest<ConnectMessage_PING>() {
  override val message = ConnectMessage_PING
  override val byteString = "50,49,4E,47,00"
}
