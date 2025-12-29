package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.GameKick

class GameKickTest : NewV086MessageTest<GameKick>() {
  override val message = GameKick(messageNumber = MESSAGE_NUMBER, userId = 13)
  override val byteString = "00, 0D, 00"
}
