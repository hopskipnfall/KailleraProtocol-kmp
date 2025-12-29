package io.github.hopskipnfall.kaillera.protocol.netty.v086.SYNCHRONIZING

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NewV086MessageTest
import io.github.hopskipnfall.kaillera.protocol.v086.GameStatus

class GameStatusTest : NewV086MessageTest<GameStatus>() {
  override val message =
    GameStatus(
      messageNumber = MESSAGE_NUMBER,
      gameId = 13,
      val1 = 2345,
      gameStatus = io.github.hopskipnfall.kaillera.protocol.model.GameStatus.SYNCHRONIZING,
      numPlayers = 4,
      maxPlayers = 4,
    )
  override val byteString = "00, 0D, 00, 29, 09, 01, 04, 04"
}
