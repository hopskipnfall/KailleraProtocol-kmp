package org.emulinker.kaillera.protocol.v086

class GameDataTest : V086MessageTest<GameData>() {
  companion object {
    const val MESSAGE_NUMBER = 42
  }

  override val message =
    GameData(messageNumber = MESSAGE_NUMBER, gameData = byteArrayOf(2, 3, 4, 5, 6))

  override val byteString = "00, 05, 00, 02, 03, 04, 05, 06"
  override val serializer = GameData.GameDataSerializer
}
