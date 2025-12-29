package io.github.hopskipnfall.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyMessageFactory
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.assertBufferContainsExactly
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import io.netty.buffer.Unpooled
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import kotlinx.io.Buffer
import org.junit.Test

abstract class V086MessageTest<K : V086Message> : ProtocolBaseTest() {
  abstract val message: K
  abstract val byteString: String

  @Test
  fun bodyLength() {
    assertThat(message.bodyBytes).isEqualTo(hexStringToByteArray(byteString).size)
  }

  @Test
  fun read() {
    val buffer = Buffer()
    message.writeBodyTo(buffer, charset = globalCharset.name())

    val deserialized =
      MessageFactory.read(
        buffer,
        message.messageNumber,
        message.messageTypeId,
        charset = globalCharset.name(),
      )

    assertThat(deserialized).isEqualTo(message)
    assertThat(buffer.size).isEqualTo(0)
  }

  @Test
  fun write() {
    val buffer = Buffer()
    message.writeBodyTo(buffer, charset = globalCharset.name())

    assertThat(buffer.size).isEqualTo(message.bodyBytes)
    MessageTestUtils.assertBufferContainsExactly(buffer, byteString)
  }

  @Test
  fun nettyRead() {
    val byteBuf = Unpooled.buffer(4096)
    byteBuf.writeBytes(hexStringToByteBuffer(byteString))

    val deserialized =
      NettyMessageFactory.read(
        message.messageNumber,
        message.messageTypeId,
        byteBuf,
        charset = globalCharset,
      )
    assertThat(deserialized).isEqualTo(message)
    assertThat(byteBuf.readableBytes()).isEqualTo(0)
  }

  @Test
  fun nettyWrite() {
    val byteBuf = Unpooled.buffer(4096)

    NettyMessageFactory.write(byteBuf, message, charset = globalCharset)

    assertThat(byteBuf.readableBytes()).isEqualTo(message.bodyBytes)
    MessageTestUtils.assertBufferContainsExactly(byteBuf, byteString)
  }
}

fun hexStringToByteBuffer(hex: String): ByteBuffer {
  fun hexStringToByteArray2(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
      data[i / 2] = ((s[i].digitToInt(16) shl 4) + s[i + 1].digitToInt(16)).toByte()
      i += 2
    }
    return data
  }

  var hex = hex
  hex = hex.replace(", ", "").replace(",", "").replace(" ", "").lowercase()
  val bytes = hexStringToByteArray2(hex)
  val buffer = ByteBuffer.allocate(bytes.size)
  buffer.order(LITTLE_ENDIAN)
  buffer.put(bytes)
  buffer.position(0)
  return buffer
}
