package org.emulinker.kaillera.protocol.netty.connection

import com.google.common.truth.Truth.assertThat
import io.netty.buffer.Unpooled
import java.nio.charset.Charset
import org.emulinker.kaillera.protocol.connection.ConnectMessage_PING
import org.emulinker.kaillera.protocol.connection.ConnectMessage_PONG
import org.emulinker.kaillera.protocol.connection.ConnectMessage_ServerFull
import org.emulinker.kaillera.protocol.connection.RequestPrivateKailleraPortRequest
import org.emulinker.kaillera.protocol.connection.RequestPrivateKailleraPortResponse
import org.junit.Test

class ConnectMessageTests {

  @Test
  fun testHello() {
    // HELLO0.86\0
    val original = RequestPrivateKailleraPortRequest("0.86")
    val buffer = Unpooled.buffer()
    NettyConnectMessageFactory.write(buffer, original)

    val str = buffer.toString(CHARSET)
    assertThat(str).isEqualTo("HELLO0.86\u0000")

    buffer.resetReaderIndex()
    val parsed = NettyConnectMessageFactory.read(buffer)
    assertThat(parsed).isInstanceOf(RequestPrivateKailleraPortRequest::class.java)
    parsed as RequestPrivateKailleraPortRequest
    assertThat(parsed.protocol).isEqualTo("0.86")
  }

  @Test
  fun testHelloD() {
    // HELLOD00D12345\0
    val original = RequestPrivateKailleraPortResponse(12345)
    val buffer = Unpooled.buffer()
    NettyConnectMessageFactory.write(buffer, original)

    val str = buffer.toString(CHARSET)
    assertThat(str).isEqualTo("HELLOD00D12345\u0000")

    buffer.resetReaderIndex()
    val parsed = NettyConnectMessageFactory.read(buffer)
    assertThat(parsed).isInstanceOf(RequestPrivateKailleraPortResponse::class.java)
    parsed as RequestPrivateKailleraPortResponse
    assertThat(parsed.port).isEqualTo(12345)
  }

  @Test
  fun testPing() {
    // PING\0
    val buffer = Unpooled.buffer()
    NettyConnectMessageFactory.write(buffer, ConnectMessage_PING)

    val str = buffer.toString(CHARSET)
    assertThat(str).isEqualTo("PING\u0000")

    buffer.resetReaderIndex()
    val parsed = NettyConnectMessageFactory.read(buffer)
    assertThat(parsed).isEqualTo(ConnectMessage_PING)
  }

  @Test
  fun testPong() {
    // PONG\0
    val buffer = Unpooled.buffer()
    NettyConnectMessageFactory.write(buffer, ConnectMessage_PONG)

    val str = buffer.toString(CHARSET)
    assertThat(str).isEqualTo("PONG\u0000")

    buffer.resetReaderIndex()
    val parsed = NettyConnectMessageFactory.read(buffer)
    assertThat(parsed).isEqualTo(ConnectMessage_PONG)
  }

  @Test
  fun testServerFull() {
    // TOO\0
    val buffer = Unpooled.buffer()
    NettyConnectMessageFactory.write(buffer, ConnectMessage_ServerFull)

    val str = buffer.toString(CHARSET)
    assertThat(str).isEqualTo("TOO\u0000")

    buffer.resetReaderIndex()
    val parsed = NettyConnectMessageFactory.read(buffer)
    assertThat(parsed).isEqualTo(ConnectMessage_ServerFull)
  }

  companion object {
    private val CHARSET = Charset.forName("ISO-8859-1")
  }
}
