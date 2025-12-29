package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.ConnectMessageTest

class ConnectMessagePongTest : ConnectMessageTest<ConnectMessage_PONG>() {
  override val message = ConnectMessage_PONG
  override val byteString = "50,4F,4E,47,00"
}
