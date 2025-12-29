package io.github.hopskipnfall.kaillera.protocol.v086

class GameDataTest : V086MessageTest<GameData>() {
  override val message =
    GameData(messageNumber = MESSAGE_NUMBER, gameData = byteArrayOf(2, 3, 4, 5, 6))
  override val byteString = "00, 05, 00, 02, 03, 04, 05, 06"
}
