package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.CloseGame

class CloseGameTest : NewV086MessageTest<CloseGame>() {
  override val message = CloseGame(messageNumber = 42, gameId = 10, val1 = 999)
  override val byteString = "00, 0A, 00, E7, 03"
}
