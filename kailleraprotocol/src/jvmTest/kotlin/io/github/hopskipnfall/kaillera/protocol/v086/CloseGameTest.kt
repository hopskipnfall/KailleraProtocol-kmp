package io.github.hopskipnfall.kaillera.protocol.v086

class CloseGameTest : V086MessageTest<CloseGame>() {
  override val message = CloseGame(messageNumber = 42, gameId = 10, val1 = 999)
  override val byteString = "00, 0A, 00, E7, 03"
}
