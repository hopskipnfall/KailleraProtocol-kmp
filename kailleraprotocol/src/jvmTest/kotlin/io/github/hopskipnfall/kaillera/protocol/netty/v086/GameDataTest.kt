package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.GameData

class GameDataTest : NewV086MessageTest<GameData>() {
  override val message =
    GameData(messageNumber = MESSAGE_NUMBER, gameData = byteArrayOf(2, 3, 4, 5, 6))
  override val byteString = "00, 05, 00, 02, 03, 04, 05, 06"
}
