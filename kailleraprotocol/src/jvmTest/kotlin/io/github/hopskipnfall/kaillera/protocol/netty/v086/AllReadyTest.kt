package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.AllReady

class AllReadyTest : NewV086MessageTest<AllReady>() {
  override val message = AllReady(42)
  override val byteString = "00"
}
