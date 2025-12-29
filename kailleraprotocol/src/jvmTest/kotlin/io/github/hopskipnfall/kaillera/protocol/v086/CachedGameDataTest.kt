package io.github.hopskipnfall.kaillera.protocol.v086

class CachedGameDataTest : V086MessageTest<CachedGameData>() {
  override val message = CachedGameData(42, key = 12)
  override val byteString = "00, 0C"
}
