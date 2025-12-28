package org.emulinker.kaillera.protocol.connection

object ConnectMessage_PONG : ConnectMessage {
  const val ID = "PONG"
  override val id: String = ID

  override fun toString(): String = "Server Pong"
}
