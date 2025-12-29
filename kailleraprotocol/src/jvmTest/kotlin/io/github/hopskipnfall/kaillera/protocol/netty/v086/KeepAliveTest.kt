package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.KeepAlive

class KeepAliveTest : NewV086MessageTest<KeepAlive>() {
  override val message = KeepAlive(MESSAGE_NUMBER, value = 12)
  override val byteString = "0C"
}
