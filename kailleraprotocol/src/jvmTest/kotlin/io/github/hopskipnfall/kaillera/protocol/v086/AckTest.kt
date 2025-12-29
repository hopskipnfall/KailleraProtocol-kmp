package io.github.hopskipnfall.kaillera.protocol.v086

class ClientAckTest : V086MessageTest<ClientAck>() {
  override val message = ClientAck(MESSAGE_NUMBER)
  override val byteString = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
}

class ServerAckTest : V086MessageTest<ServerAck>() {
  override val message = ServerAck(MESSAGE_NUMBER)
  override val byteString = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
}
