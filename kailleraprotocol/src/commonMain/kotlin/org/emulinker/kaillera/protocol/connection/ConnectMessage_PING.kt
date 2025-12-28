package org.emulinker.kaillera.protocol.connection

object ConnectMessage_PING : ConnectMessage {
  const val ID = "PING"
  override val id: String = ID

  override fun toString(): String = "Client Ping"
}
