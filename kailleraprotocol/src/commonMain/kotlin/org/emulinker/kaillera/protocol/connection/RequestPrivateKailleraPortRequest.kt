package org.emulinker.kaillera.protocol.connection

data class RequestPrivateKailleraPortRequest(val protocol: String) : ConnectMessage {
  override val id: String = ID

  companion object {
    const val ID = "HELLO"
  }
}
