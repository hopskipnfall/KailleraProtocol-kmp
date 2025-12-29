package io.github.hopskipnfall.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.connection.ConnectMessage
import io.github.hopskipnfall.kaillera.protocol.connection.ConnectMessageFactory
import io.github.hopskipnfall.kaillera.protocol.netty.connection.NettyConnectMessageFactory
import io.netty.buffer.Unpooled
import kotlinx.io.Buffer
import kotlinx.io.write
import org.junit.Test

abstract class ConnectMessageTest<K : ConnectMessage> : ProtocolBaseTest() {
  abstract val message: K
  abstract val byteString: String

  @OptIn(ExperimentalStdlibApi::class)
  @Test
  fun read() {
    val buffer = Buffer()
    buffer.write(hexStringToByteBuffer(byteString))

    val deserialized = ConnectMessageFactory.read(buffer)

    assertThat(deserialized).isEqualTo(message)
    assertThat(buffer.size).isEqualTo(0)
  }

  @Test
  fun write() {
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    MessageTestUtils.assertBufferContainsExactly(buffer, byteString)
  }

  @Test
  fun nettyRead() {
    val byteBuf = Unpooled.buffer(4096)
    byteBuf.writeBytes(hexStringToByteBuffer(byteString))

    val deserialized = NettyConnectMessageFactory.read(byteBuf)

    assertThat(deserialized).isEqualTo(message)
    assertThat(byteBuf.readableBytes()).isEqualTo(0)
  }

  @Test
  fun nettyWrite() {
    val byteBuf = Unpooled.buffer(4096)
    NettyConnectMessageFactory.write(byteBuf, message)

    MessageTestUtils.assertBufferContainsExactly(byteBuf, byteString)
    byteBuf.release()
  }
}
