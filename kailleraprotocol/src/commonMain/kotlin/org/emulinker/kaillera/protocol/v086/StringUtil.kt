package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

expect object StringUtil {
  fun encode(s: String, charset: String): ByteArray

  fun decode(bytes: ByteArray, charset: String): String

  fun readString(source: Source, charset: String): String

  fun writeString(sink: Sink, s: String, charset: String)
}
