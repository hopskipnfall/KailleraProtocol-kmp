package io.github.hopskipnfall.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.v086.ClientAck
import io.github.hopskipnfall.kaillera.protocol.v086.KeepAlive
import io.github.hopskipnfall.kaillera.protocol.v086.MessageSerializer
import io.github.hopskipnfall.kaillera.protocol.v086.Quit
import io.github.hopskipnfall.kaillera.protocol.v086.QuitNotification
import io.github.hopskipnfall.kaillera.protocol.v086.QuitRequest
import io.github.hopskipnfall.kaillera.protocol.v086.ServerAck
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import io.github.hopskipnfall.kaillera.protocol.v086.V086Message
import io.netty.buffer.Unpooled
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.junit.Test

class SimpleMessageTests {

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
  fun keepAlive() {
    val msg = KeepAlive(42, 12)
    val hex = "0C"
    testKmp(KeepAlive.KeepAliveSerializer, msg, hex)
    testNetty(NettyKeepAliveSerializer::read, NettyKeepAliveSerializer::write, msg, hex)
  }

  @Test
  fun clientAck() {
    val msg = ClientAck(42)
    val hex = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
    testKmp(ClientAck.ClientAckSerializer, msg, hex)
    testNetty(NettyClientAckSerializer::read, NettyClientAckSerializer::write, msg, hex)
  }

  @Test
  fun serverAck() {
    // Validation logic allows 0,1,2,3 defaults
    val msg = ServerAck(42)
    val hex = "00, 00, 00, 00, 00, 01, 00, 00, 00, 02, 00, 00, 00, 03, 00, 00, 00"
    testKmp(ServerAck.ServerAckSerializer, msg, hex)
    testNetty(NettyServerAckSerializer::read, NettyServerAckSerializer::write, msg, hex)
  }

  @Test
  fun quitRequest() {
    val msg = QuitRequest(42, "Hello, world!")
    val hex = "00, FF, FF, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
    testKmp(Quit.QuitSerializer, msg, hex)
    testNetty(NettyQuitSerializer::read, NettyQuitSerializer::write, msg, hex)
  }

  @Test
  fun quitNotification() {
    val msg = QuitNotification(42, "nue", 13, "Hello, world!")
    val hex = "6E, 75, 65, 00, 0D, 00, 48, 65, 6C, 6C, 6F, 2C, 20, 77, 6F, 72, 6C, 64, 21, 00"
    testKmp(Quit.QuitSerializer, msg, hex)
    testNetty(NettyQuitSerializer::read, NettyQuitSerializer::write, msg, hex)
  }
}
