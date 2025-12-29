package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.CachedGameData

class CachedGameDataTest : NewV086MessageTest<CachedGameData>() {
  override val message = CachedGameData(42, key = 12)
  override val byteString = "00, 0C"
}
