package org.emulinker.kaillera.protocol.v086

import kotlinx.cinterop.*
import kotlinx.io.Sink
import kotlinx.io.Source
import platform.posix.*

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
      // Simplistic casting to bytes (ISO-8859-1 maps directly)
      val bytes = ByteArray(s.length)
      for (i in s.indices) {
        bytes[i] = s[i].code.toByte()
      }
      return bytes
    }

    // TODO: Full iconv support requires cinterop configuration for iconv.h which is not default in
    // standard platform.posix on all targets.
    // For now, defaulting to UTF-8 for everything else.
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
