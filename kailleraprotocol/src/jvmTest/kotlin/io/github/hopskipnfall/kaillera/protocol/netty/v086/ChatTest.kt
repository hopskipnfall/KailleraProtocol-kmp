package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.Chat

class ChatRequestTest : NewV086MessageTest<Chat>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.ChatRequest(
      messageNumber = 42,
      message = "Hello, world!",
    )
  override val byteString = "00,48,65,6C,6C,6F,2C,20,77,6F,72,6C,64,21,00"
}

class ChatNotificationTest : NewV086MessageTest<Chat>() {
  override val message =
    io.github.hopskipnfall.kaillera.protocol.v086.ChatNotification(
      messageNumber = 42,
      username = "nue",
      message = "Hello, world!",
    )
  override val byteString = "6E,75,65,00,48,65,6C,6C,6F,2C,20,77,6F,72,6C,64,21,00"
}
