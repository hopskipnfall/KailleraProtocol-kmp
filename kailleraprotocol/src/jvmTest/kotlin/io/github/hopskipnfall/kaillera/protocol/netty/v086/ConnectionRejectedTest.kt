package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.ConnectionRejected

class ConnectionRejectedTest : NewV086MessageTest<ConnectionRejected>() {
  override val message =
    ConnectionRejected(
      messageNumber = 42,
      username = "nue",
      userId = 100,
      message = "This is a message!",
    )
  override val byteString =
    "6E, 75, 65, 00, 64, 00, 54, 68, 69, 73, 20, 69, 73, 20, 61, 20, 6D, 65, 73, 73, 61, 67, 65, 21, 00"
}
