package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NewConnectMessageTest

class ConnectMessagePongTest : NewConnectMessageTest<ConnectMessage_PONG>() {
  override val message = ConnectMessage_PONG
  override val byteString = "50,4F,4E,47,00"
}
