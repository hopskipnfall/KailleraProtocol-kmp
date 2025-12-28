package org.emulinker.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import kotlinx.io.Buffer
import kotlinx.io.readByteArray

object TestUtils {
  fun hexStringToByteArray(s: String): ByteArray {
    val clean = s.replace(" ", "").replace(",", "")
    val len = clean.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
      data[i / 2] =
        ((Character.digit(clean[i], 16) shl 4) + Character.digit(clean[i + 1], 16)).toByte()
      i += 2
    }
    return data
  }

  fun assertBufferContainsExactly(buffer: Buffer, byteString: String) {
    val actualBytes = buffer.readByteArray()
    // Determine expected bytes
    val expectedBytes = hexStringToByteArray(byteString)

    assertThat(actualBytes).isEqualTo(expectedBytes)
  }

  fun ByteArray.toHexString(): String {
    return joinToString(", ") { "%02x".format(it) }
  }
}
