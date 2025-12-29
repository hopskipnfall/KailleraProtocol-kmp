package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NewConnectMessageTest

class RequestPrivateKailleraPortRequestTest :
  NewConnectMessageTest<RequestPrivateKailleraPortRequest>() {
  override val message = RequestPrivateKailleraPortRequest(protocol = "0.86")
  override val byteString = "48,45,4C,4C,4F,30,2E,38,36,00"
}
