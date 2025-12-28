package org.emulinker.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.netty.buffer.Unpooled
import org.emulinker.kaillera.protocol.v086.GameData
import org.emulinker.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import org.junit.Test

class NettyGameDataSerializerTest {

  private val message = GameData(messageNumber = 42, gameData = byteArrayOf(2, 3, 4, 5, 6))
  private val byteString = "00, 05, 00, 02, 03, 04, 05, 06"

  @Test
  fun read() {
    val bytes = hexStringToByteArray(byteString)
    val buffer = Unpooled.wrappedBuffer(bytes)

    val deserialized = NettyGameDataSerializer.read(buffer, message.messageNumber, "ISO-8859-1")

    assertThat(deserialized).isEqualTo(message)
    // buffer index should be at end if completely read, but serializer only reads specific amount.
    // check if readerIndex advanced by bytes.size
    assertThat(buffer.readerIndex()).isEqualTo(bytes.size)
  }

  @Test
  fun write() {
    val buffer = Unpooled.buffer()
    NettyGameDataSerializer.write(buffer, message, "ISO-8859-1")

    val bytesWritten = ByteArray(buffer.readableBytes())
    buffer.readBytes(bytesWritten)

    val expectedBytes = hexStringToByteArray(byteString)
    assertThat(bytesWritten).isEqualTo(expectedBytes)
  }
}
