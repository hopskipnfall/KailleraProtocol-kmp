package org.emulinker.kaillera.model

enum class UserStatus(val byteValue: Byte) {
  PLAYING(0.toByte()),
  IDLE(1.toByte()),
  CONNECTING(2.toByte());

  companion object {
    fun fromByteValue(byteValue: Byte): UserStatus {
      return entries.find { it.byteValue == byteValue } ?: CONNECTING
    }

    fun getByValue(value: Int): UserStatus = fromByteValue(value.toByte())
  }
}
