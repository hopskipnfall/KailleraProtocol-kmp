package io.github.hopskipnfall.kaillera.protocol.v086

class KeepAliveTest : V086MessageTest<KeepAlive>() {
  override val message = KeepAlive(MESSAGE_NUMBER, value = 12)
  override val byteString = "0C"
}
