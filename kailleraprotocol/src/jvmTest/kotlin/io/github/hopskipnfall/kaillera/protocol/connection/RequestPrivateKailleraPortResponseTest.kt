package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.ConnectMessageTest

class RequestPrivateKailleraPortResponseTest :
  ConnectMessageTest<RequestPrivateKailleraPortResponse>() {
  override val message = RequestPrivateKailleraPortResponse(port = 424242)
  override val byteString = "48,45,4C,4C,4F,44,30,30,44,34,32,34,32,34,32,00"
}
