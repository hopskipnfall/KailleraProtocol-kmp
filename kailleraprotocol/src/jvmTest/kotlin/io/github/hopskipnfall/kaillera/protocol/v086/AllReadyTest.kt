package io.github.hopskipnfall.kaillera.protocol.v086

class AllReadyTest : V086MessageTest<AllReady>() {
  override val message = AllReady(42)
  override val byteString = "00"
}
