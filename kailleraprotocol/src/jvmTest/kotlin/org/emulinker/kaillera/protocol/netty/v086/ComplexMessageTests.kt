package org.emulinker.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.netty.buffer.Unpooled
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.emulinker.kaillera.model.ConnectionType
import org.emulinker.kaillera.protocol.v086.Chat
import org.emulinker.kaillera.protocol.v086.ChatNotification
import org.emulinker.kaillera.protocol.v086.ChatRequest
import org.emulinker.kaillera.protocol.v086.ConnectionRejected
import org.emulinker.kaillera.protocol.v086.CreateGame
import org.emulinker.kaillera.protocol.v086.CreateGameNotification
import org.emulinker.kaillera.protocol.v086.CreateGameRequest
import org.emulinker.kaillera.protocol.v086.GameChat
import org.emulinker.kaillera.protocol.v086.GameChatNotification
import org.emulinker.kaillera.protocol.v086.GameChatRequest
import org.emulinker.kaillera.protocol.v086.InformationMessage
import org.emulinker.kaillera.protocol.v086.JoinGame
import org.emulinker.kaillera.protocol.v086.JoinGameNotification
import org.emulinker.kaillera.protocol.v086.JoinGameRequest
import org.emulinker.kaillera.protocol.v086.MessageSerializer
import org.emulinker.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import org.emulinker.kaillera.protocol.v086.UserInformation
import org.emulinker.kaillera.protocol.v086.UserJoined
import org.emulinker.kaillera.protocol.v086.V086Message
import org.junit.Test

class ComplexMessageTests {

  private fun <T : V086Message> testKmp(
    serializer: MessageSerializer<T>,
    message: T,
    hexString: String,
  ) {
    val buffer = Buffer()
    serializer.write(buffer, message, "ISO-8859-1")
    val writtenBytes = buffer.readByteArray()
    val expectedBytes = hexStringToByteArray(hexString)
    if (!writtenBytes.contentEquals(expectedBytes)) {
      println(
        "KMP Write Mismatch: Expected ${expectedBytes.contentToString()}, Actual ${writtenBytes.contentToString()}"
      )
    }
    assertThat(writtenBytes).isEqualTo(expectedBytes)

    val readBuffer = Buffer()
    readBuffer.write(expectedBytes)
    val readMessage = serializer.read(readBuffer, message.messageNumber, "ISO-8859-1")
    assertThat(readMessage).isEqualTo(message)
  }

  private fun <T : V086Message> testNetty(
    readFunc: (io.netty.buffer.ByteBuf, Int, String) -> T,
    writeFunc: (io.netty.buffer.ByteBuf, T, String) -> Unit,
    message: T,
    hexString: String,
  ) {
    val buffer = Unpooled.buffer()
    writeFunc(buffer, message, "ISO-8859-1")
    val writtenBytes = ByteArray(buffer.readableBytes())
    buffer.readBytes(writtenBytes)
    val expectedBytes = hexStringToByteArray(hexString)
    assertThat(writtenBytes).isEqualTo(expectedBytes)

    val readBuffer = Unpooled.wrappedBuffer(expectedBytes)
    val readMessage = readFunc(readBuffer, message.messageNumber, "ISO-8859-1")
    assertThat(readMessage).isEqualTo(message)
  }

  @Test
  fun chatRequest() {
    val msg = ChatRequest(42, "Hello, world!")
    val hex = "00,48,65,6C,6C,6F,2C,20,77,6F,72,6C,64,21,00"
    testKmp(Chat.ChatSerializer, msg, hex)
    testNetty(NettyChatSerializer::read, NettyChatSerializer::write, msg, hex)
  }

  @Test
  fun chatNotification() {
    val msg = ChatNotification(42, "nue", "Hello, world!")
    val hex = "6E,75,65,00,48,65,6C,6C,6F,2C,20,77,6F,72,6C,64,21,00"
    testKmp(Chat.ChatSerializer, msg, hex)
    testNetty(NettyChatSerializer::read, NettyChatSerializer::write, msg, hex)
  }

  @Test
  fun gameChatRequest() {
    val msg = GameChatRequest(42, "Hello, world!")
    val hex = "00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
    testKmp(GameChat.GameChatSerializer, msg, hex)
    testNetty(NettyGameChatSerializer::read, NettyGameChatSerializer::write, msg, hex)
  }

  @Test
  fun gameChatNotification() {
    val msg = GameChatNotification(42, "nue", "Hello, world!")
    val hex = "6E, 75, 65, 00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
    testKmp(GameChat.GameChatSerializer, msg, hex)
    testNetty(NettyGameChatSerializer::read, NettyGameChatSerializer::write, msg, hex)
  }

  @Test
  fun createGameRequest() {
    val msg = CreateGameRequest(42, "My Game")
    val hex = "00, 4D, 79, 20, 47, 61, 6D, 65, 00, 00, FF, FF, FF, FF"
    testKmp(CreateGame.CreateGameSerializer, msg, hex)
    testNetty(NettyCreateGameSerializer::read, NettyCreateGameSerializer::write, msg, hex)
  }

  @Test
  fun createGameNotification() {
    val msg = CreateGameNotification(42, "nue", "My Game", "My N64 Emulator", 100, 4242)
    val hex =
      "6E, 75, 65, 00, 4D, 79, 20, 47, 61, 6D, 65, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 64, 00, 92, 10"
    testKmp(CreateGame.CreateGameSerializer, msg, hex)
    testNetty(NettyCreateGameSerializer::read, NettyCreateGameSerializer::write, msg, hex)
  }

  @Test
  fun joinGameRequest() {
    val msg = JoinGameRequest(42, 135, ConnectionType.BAD)
    val hex = "00, 87, 00, 00, 00, 00, 00, 00, 00, 00, FF, FF, 06"
    testKmp(JoinGame.JoinGameSerializer, msg, hex)
    testNetty(NettyJoinGameSerializer::read, NettyJoinGameSerializer::write, msg, hex)
  }

  @Test
  fun joinGameNotification() {
    val msg = JoinGameNotification(42, 135, 1234, "nue", 1235.milliseconds, 13, ConnectionType.BAD)
    val hex = "00, 87, 00, D2, 04, 6E, 75, 65, 00, D3, 04, 00, 00, 0D, 00, 06"
    testKmp(JoinGame.JoinGameSerializer, msg, hex)
    testNetty(NettyJoinGameSerializer::read, NettyJoinGameSerializer::write, msg, hex)
  }

  @Test
  fun userJoined() {
    val msg = UserJoined(42, "nue", 13, 999.milliseconds, ConnectionType.LAN)
    val hex = "6E, 75, 65, 00, 0D, 00, E7, 03, 00, 00, 01"
    testKmp(UserJoined.UserJoinedSerializer, msg, hex)
    testNetty(NettyUserJoinedSerializer::read, NettyUserJoinedSerializer::write, msg, hex)
  }

  @Test
  fun informationMessage() {
    val msg = InformationMessage(42, "This is a source", "Hello, world!")
    val hex =
      "54, 68, 69, 73, 20, 69, 73, 20, 61, 20, 73, 6F, 75, 72, 63, 65, 00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
    testKmp(InformationMessage.InformationMessageSerializer, msg, hex)
    testNetty(
      NettyInformationMessageSerializer::read,
      NettyInformationMessageSerializer::write,
      msg,
      hex,
    )
  }

  @Test
  fun connectionRejected() {
    val msg = ConnectionRejected(42, "nue", 100, "This is a message!")
    val hex =
      "6E, 75, 65, 00, 64, 00, 54, 68, 69, 73, 20, 69, 73, 20, 61, 20, 6D, 65, 73, 73, 61, 67, 65, 21, 00"
    testKmp(ConnectionRejected.ConnectionRejectedSerializer, msg, hex)
    testNetty(
      NettyConnectionRejectedSerializer::read,
      NettyConnectionRejectedSerializer::write,
      msg,
      hex,
    )
  }

  @Test
  fun userInformation() {
    val msg = UserInformation(42, "nue", "My Emulator", ConnectionType.LAN)
    val hex = "6E, 75, 65, 00, 4D, 79, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 01"
    testKmp(UserInformation.UserInformationSerializer, msg, hex)
    testNetty(NettyUserInformationSerializer::read, NettyUserInformationSerializer::write, msg, hex)
  }
}
