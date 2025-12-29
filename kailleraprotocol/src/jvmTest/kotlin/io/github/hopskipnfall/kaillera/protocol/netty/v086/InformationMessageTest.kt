package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.InformationMessage

class InformationMessageTest : NewV086MessageTest<InformationMessage>() {
  override val message =
    InformationMessage(
      messageNumber = MESSAGE_NUMBER,
      source = "This is a source",
      message = "Hello, world!",
    )
  override val byteString =
    "54, 68, 69, 73, 20, 69, 73, 20, 61, 20, 73, 6F, 75, 72, 63, 65, 00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
}
