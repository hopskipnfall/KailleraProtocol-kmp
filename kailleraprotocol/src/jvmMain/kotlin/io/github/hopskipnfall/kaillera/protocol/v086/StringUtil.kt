package io.github.hopskipnfall.kaillera.protocol.v086

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import kotlinx.io.Sink
import kotlinx.io.Source

actual object StringUtil {
  actual fun encode(s: String, charset: String): ByteArray {
    return s.toByteArray(Charset.forName(charset))
  }

  actual fun decode(bytes: ByteArray, charset: String): String {
    return String(bytes, Charset.forName(charset))
  }

  actual fun readString(source: Source, charset: String): String {
    val baos = ByteArrayOutputStream()
    while (!source.exhausted()) {
      val b = source.readByte()
      if (b == 0.toByte()) {
        break
      }
      baos.write(b.toInt())
    }
    return String(baos.toByteArray(), Charset.forName(charset))
  }

  actual fun writeString(sink: Sink, s: String, charset: String) {
    val bytes = s.toByteArray(Charset.forName(charset))
    sink.write(bytes)
    sink.writeByte(0)
  }
}
