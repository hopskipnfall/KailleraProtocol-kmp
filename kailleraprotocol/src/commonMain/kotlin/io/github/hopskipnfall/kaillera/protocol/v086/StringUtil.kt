package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

/**
 * Utility for encoding and decoding strings with support for legacy charsets used in Kaillera.
 *
 * Supported Encodings:
 * - **ISO-8859-1** / **Latin-1**: Optimized and supported across all targets (JVM, Native, JS).
 * - **UTF-8**: Supported across all targets.
 * - **Shift_JIS** (Japanese): Supported on JVM. Supported on Native/JS if underlying host/browser
 *   supports it.
 * - **Windows-1251** (Cyrillic): Supported on JVM. Supported on Native/JS if underlying
 *   host/browser supports it.
 * - **EUC-KR** (Korean): Supported on JVM. Supported on Native/JS if underlying host/browser
 *   supports it.
 *
 * Note: On Native targets (Linux, macOS, Windows), legacy charsets (Shift_JIS, etc.) usage depends
 * on the availability of system libraries (iconv on Unix, WinAPI on Windows) and proper
 * configuration. Fallback to UTF-8/ISO-8859-1 may occur if strictly required libraries are missing.
 */
expect object StringUtil {
  fun encode(s: String, charset: String): ByteArray

  fun decode(bytes: ByteArray, charset: String): String

  fun readString(source: Source, charset: String): String

  fun writeString(sink: Sink, s: String, charset: String)
}
