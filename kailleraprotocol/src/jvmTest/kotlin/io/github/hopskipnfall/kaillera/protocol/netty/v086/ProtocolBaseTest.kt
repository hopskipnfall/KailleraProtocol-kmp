package io.github.hopskipnfall.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.netty.v086.MessageTestUtils.assertBufferContainsExactly
import io.github.hopskipnfall.kaillera.protocol.v086.MessageFactory
import io.github.hopskipnfall.kaillera.protocol.v086.V086Message
import io.netty.buffer.Unpooled
import java.nio.ByteBuffer
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.charset.Charset
import kotlinx.io.Buffer
import org.junit.BeforeClass
import org.junit.Test

// abstract class ConnectMessageTest<K : ConnectMessage> : ProtocolBaseTest() {
//  abstract val message: K
//  abstract val byteString: String
//
//  @Test
//  fun parse() {
//    val byteBuf = Unpooled.buffer(4096)
//    byteBuf.writeBytes(hexStringToByteBuffer(byteString))
//
//    val deserialized = ConnectMessage.parse(byteBuf)
//
//    assertThat(deserialized.getOrThrow()).isEqualTo(message)
//    assertThat(byteBuf.readableBytes()).isEqualTo(0)
//  }
//
//  @Test
//  fun write() {
//    val byteBuf = Unpooled.buffer(4096)
//    message.writeTo(byteBuf)
//
//    assertBufferContainsExactly(byteBuf, byteString)
//  }
// }

abstract class ProtocolBaseTest {
  companion object {
    protected const val MESSAGE_NUMBER = 42

    lateinit var globalCharset: Charset

    @BeforeClass
    @JvmStatic
    fun setup() {
      globalCharset = Charset.forName("Shift_JIS")
    }
  }
}

abstract class NewV086MessageTest<K : V086Message> : ProtocolBaseTest() {
  abstract val message: K
  abstract val byteString: String

  @Test
  fun newBodyLength() {
    assertThat(message.bodyBytes).isEqualTo(message.bodyBytes)
  }

  @Test
  fun ktxRead() {
    val buffer = Buffer()
    message.writeTo(buffer, charset = globalCharset.name())

    val deserialized =
      MessageFactory.read(
        buffer,
        message.messageNumber,
        message.messageTypeId,
        charset = globalCharset.name(),
      )

    assertThat(deserialized).isEqualTo(message)
    //    assertThat(buffer.remaining).isEqualTo(0)
  }

  @Test
  fun ktxWrite() {
    val buffer = Buffer()
    message.writeTo(buffer, charset = globalCharset.name())

    //    assertThat(buffer.remaining).isEqualTo(newMessage.bodyBytes)
    assertBufferContainsExactly(buffer, byteString)
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
        charset = globalCharset.name(),
      )
    assertThat(deserialized).isEqualTo(message)
    assertThat(byteBuf.readableBytes()).isEqualTo(0)
  }

  @Test
  fun nettyWrite() {
    val byteBuf = Unpooled.buffer(4096)

    NettyMessageFactory.write(byteBuf, message, charset = globalCharset.name())

    assertThat(byteBuf.readableBytes()).isEqualTo(message.bodyBytes)
    assertBufferContainsExactly(byteBuf, byteString)
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
  //    buffer.limit(hex.length / 2)
  return buffer
}
