package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

actual object StringUtil {
  actual fun encode(s: String, charset: String): ByteArray {
    // JS TextEncoder only supports UTF-8 generally.
    // For other charsets on JS, we might need a polyfill or library, but for now we'll stick to
    // simplified support
    // or just error if not UTF-8 if strictly required, OR use a simple hack.
    // However, the prompt says "best effort".
    // If charset is "ISO-8859-1", we can just cast chars to bytes.
    if (charset.uppercase() == "ISO-8859-1" || charset.uppercase() == "US-ASCII") {
      val bytes = ByteArray(s.length)
      for (i in s.indices) {
        bytes[i] = s[i].code.toByte()
      }
      return bytes
    }
    // Fallback to UTF-8 (TextEncoder is standard in modern JS envs)
    return TextEncoder().encode(s)
  }

  actual fun decode(bytes: ByteArray, charset: String): String {
    if (charset.uppercase() == "ISO-8859-1" || charset.uppercase() == "US-ASCII") {
      val sb = StringBuilder()
      for (b in bytes) {
        sb.append((b.toInt() and 0xFF).toChar())
      }
      return sb.toString()
    }
    // Attempt to use arbitrary charset label with TextDecoder
    return try {
      TextDecoder(charset).decode(bytes)
    } catch (e: dynamic) {
      // Fallback or rethrow?
      // If the browser doesn't support the charset label, it might throw RangeError
      // Fallback to decodeToString (UTF-8)
      bytes.decodeToString()
    }
  }

  actual fun readString(source: Source, charset: String): String {
    // Read until 0
    // kotlinx-io Source in JS doesn't have a convenient dynamic array builder without copying?
    // We can accumulate bytes.
    val bytes = ArrayList<Byte>()
    while (!source.exhausted()) {
      val b = source.readByte()
      if (b == 0.toByte()) {
        break
      }
      bytes.add(b)
    }
    return decode(bytes.toByteArray(), charset)
  }

  actual fun writeString(sink: Sink, s: String, charset: String) {
    val bytes = encode(s, charset)
    sink.write(bytes)
    sink.writeByte(0)
  }
}

// External declarations for JS TextEncoder/Decoder if not available in stdlib for JS target yet
// (Kotlin 2.1 might have them? No, usually browser APIs).
external class TextEncoder() {
  fun encode(input: String): ByteArray
}

external class TextDecoder(label: String = definedExternally) {
  fun decode(input: ByteArray): String
}
