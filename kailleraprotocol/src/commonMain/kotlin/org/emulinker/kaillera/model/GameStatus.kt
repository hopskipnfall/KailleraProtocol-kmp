package org.emulinker.kaillera.model

enum class GameStatus(val byteValue: Byte) {
  WAITING(0.toByte()),
  SYNCHRONIZING(1.toByte()),
  PLAYING(2.toByte());

  companion object {
    fun fromByteValue(byteValue: Byte): GameStatus {
      return entries.find { it.byteValue == byteValue } ?: WAITING
    }

    fun getByValue(value: Int): GameStatus = fromByteValue(value.toByte())
  }
}
