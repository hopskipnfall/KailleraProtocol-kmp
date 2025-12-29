package io.github.hopskipnfall.kaillera.protocol.connection

data class RequestPrivateKailleraPortResponse(val port: Int) : ConnectMessage {
  override val id: String = ID

  companion object {
    const val ID = "HELLOD00D"
  }
}
