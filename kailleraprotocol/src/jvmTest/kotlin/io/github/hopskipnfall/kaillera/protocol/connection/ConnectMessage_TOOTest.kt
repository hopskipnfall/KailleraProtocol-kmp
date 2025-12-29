package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NewConnectMessageTest

class ConnectMessageTooTest : NewConnectMessageTest<ConnectMessage_ServerFull>() {
  override val message = ConnectMessage_ServerFull
  override val byteString = "54,4F,4F,00"
}
