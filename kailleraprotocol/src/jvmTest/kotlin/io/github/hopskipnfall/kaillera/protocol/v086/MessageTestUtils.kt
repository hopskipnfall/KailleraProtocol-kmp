package io.github.hopskipnfall.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import io.netty.buffer.ByteBuf
import java.nio.ByteBuffer
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

@OptIn(ExperimentalStdlibApi::class)
object MessageTestUtils : ProtocolBaseTest() {

  fun assertBufferContainsExactly(buffer: ByteBuffer, byteString: String) {
    val numberWritten = buffer.position()
    val stringForm = buffer.dumpToByteArray().take(numberWritten).toByteArray().toHexString()

    assertThat(stringForm).isEqualTo(byteString.replace(" ", "").replace(",", "").lowercase())

    stringForm.split(",").drop(numberWritten).forEach { assertThat(it).isEqualTo("00") }
  }

  fun assertBufferContainsExactly(buffer: ByteBuf, byteString: String) {
    assertThat(buffer.dumpToByteArray().toHexString())
      .isEqualTo(byteString.replace(" ", "").replace(",", "").lowercase())
  }

  fun assertBufferContainsExactly(buffer: Buffer, byteString: String) {
    assertThat(buffer.readByteArray().toHexString())
      .isEqualTo(byteString.replace(" ", "").replace(",", "").lowercase())
  }
}

private fun ByteBuf.dumpToByteArray(): ByteArray {
  val arr = ByteArray(this.readableBytes() + this.readerIndex())
  this.getBytes(0, arr)
  return arr
}

private fun ByteBuffer.dumpToByteArray(): ByteArray {
  val pos = position()
  position(0)
  val arr = ByteArray(remaining())
  get(/* index= */ 0, arr)
  position(pos)
  return arr
}
