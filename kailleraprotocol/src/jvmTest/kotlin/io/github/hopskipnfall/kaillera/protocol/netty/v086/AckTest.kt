package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.ClientAck
import io.github.hopskipnfall.kaillera.protocol.v086.ServerAck

class ClientAckTest : NewV086MessageTest<ClientAck>() {
  override val message = ClientAck(MESSAGE_NUMBER)
  override val byteString = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
}

class ServerAckTest : NewV086MessageTest<ServerAck>() {
  override val message = ServerAck(MESSAGE_NUMBER)
  override val byteString = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
}
