package org.emulinker.kaillera.protocol.connection

object ConnectMessage_ServerFull : ConnectMessage {
  const val ID = "TOO"
  override val id: String = ID

  override fun toString(): String = "Server Full Response"
}
