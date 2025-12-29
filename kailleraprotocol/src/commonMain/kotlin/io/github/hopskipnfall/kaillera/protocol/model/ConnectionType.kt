package io.github.hopskipnfall.kaillera.protocol.model

enum class ConnectionType(val byteValue: Byte) {
  DISABLED(0.toByte()),
  LAN(1.toByte()),
  EXCELLENT(2.toByte()),
  GOOD(3.toByte()),
  AVERAGE(4.toByte()),
  LOW(5.toByte()),
  BAD(6.toByte());

  companion object {
    fun fromByteValue(byteValue: Byte): ConnectionType {
      return entries.find { it.byteValue == byteValue } ?: LAN
    }
  }
}
