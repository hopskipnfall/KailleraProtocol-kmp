package io.github.hopskipnfall.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.v086.MessageSerializer
import io.github.hopskipnfall.kaillera.protocol.v086.StartGame
import io.github.hopskipnfall.kaillera.protocol.v086.StartGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.StartGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import io.github.hopskipnfall.kaillera.protocol.v086.V086Message
import io.netty.buffer.Unpooled
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.junit.Test

class StartGameTests {

  private fun <T : V086Message> testKmp(
    serializer: MessageSerializer<T>,
    message: T,
    hexString: String,
  ) {
    val buffer = Buffer()
    serializer.write(buffer, message, "ISO-8859-1")
    val writtenBytes = buffer.readByteArray()
    val expectedBytes = hexStringToByteArray(hexString)
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
  fun startGameRequest() {
    val msg = StartGameRequest(42)
    val hex = "00, FF, FF, FF, FF"
    testKmp(StartGame.StartGameSerializer, msg, hex)
    testNetty(NettyStartGameSerializer::read, NettyStartGameSerializer::write, msg, hex)
  }

  @Test
  fun startGameNotification() {
    val msg = StartGameNotification(42, 4, 42, 2000)
    val hex = "00, D0, 07, 2A, 04"
    testKmp(StartGame.StartGameSerializer, msg, hex)
    testNetty(NettyStartGameSerializer::read, NettyStartGameSerializer::write, msg, hex)
  }
}
