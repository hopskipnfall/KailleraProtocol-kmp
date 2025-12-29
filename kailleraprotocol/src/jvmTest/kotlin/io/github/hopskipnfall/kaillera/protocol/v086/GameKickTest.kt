package io.github.hopskipnfall.kaillera.protocol.v086

class GameKickTest : V086MessageTest<GameKick>() {
  override val message = GameKick(messageNumber = MESSAGE_NUMBER, userId = 13)
  override val byteString = "00, 0D, 00"
}
