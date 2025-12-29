package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.Sink
import kotlinx.io.Source

@OptIn(ExperimentalForeignApi::class)
actual object StringUtil {
  actual fun encode(s: String, charset: String): ByteArray {
    // Manual fallback for ISO-8859-1 / ASCII
    val upper = charset.uppercase()
    if (
      upper == "ISO-8859-1" ||
        upper == "ISO8859-1" ||
        upper == "LATIN1" ||
        upper == "US-ASCII" ||
        upper == "ASCII"
    ) {
      val bytes = ByteArray(s.length)
      for (i in s.indices) {
        bytes[i] = s[i].code.toByte()
      }
      return bytes
    }

    // Fallback to UTF-8 (WinAPI requires specific cinterop setup matching the host toolchain
    // version)
    return s.encodeToByteArray()
  }

  actual fun decode(bytes: ByteArray, charset: String): String {
    if (bytes.isEmpty()) return ""
    val upper = charset.uppercase()
    if (
      upper == "ISO-8859-1" ||
        upper == "ISO8859-1" ||
        upper == "LATIN1" ||
        upper == "US-ASCII" ||
        upper == "ASCII"
    ) {
      val sb = StringBuilder(bytes.size)
      for (b in bytes) {
        sb.append((b.toInt() and 0xFF).toChar())
      }
      return sb.toString()
    }

    return bytes.decodeToString()
  }

  private fun getCodePage(charset: String): UInt {
    // Placeholder to keep the helper if we re-enable WinAPI later,
    // or just remove it to suppress unused warning.
    // Removing unused code for clean build.
    return 65001.toUInt() // CP_UTF8
  }

  actual fun readString(source: Source, charset: String): String {
    val bytes = ArrayList<Byte>()
    while (!source.exhausted()) {
      val b = source.readByte()
      if (b == 0.toByte()) {
        break
      }
      bytes.add(b)
    }
    val byteArray = ByteArray(bytes.size)
    for (i in bytes.indices) {
      byteArray[i] = bytes[i]
    }
    return decode(byteArray, charset)
  }

  actual fun writeString(sink: Sink, s: String, charset: String) {
    val bytes = encode(s, charset)
    sink.write(bytes)
    sink.writeByte(0)
  }
}
