package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset

object NettyStringUtil {
  fun readString(buffer: ByteBuf, stopByte: Int = 0x00, charset: Charset): String {
    val len = buffer.bytesBefore(stopByte.toByte())
    if (len == -1) {
      val remaining = buffer.readableBytes()
      val bytes = ByteArray(remaining)
      buffer.readBytes(bytes)
      return String(bytes, charset)
    }
    val bytes = ByteArray(len)
    buffer.readBytes(bytes)
    buffer.readByte() // consume stop byte
    return String(bytes, charset)
  }

  fun writeString(buffer: ByteBuf, str: String, stopByte: Int = 0x00, charset: Charset) {
    val bytes = str.toByteArray(charset)
    buffer.writeBytes(bytes)
    buffer.writeByte(stopByte)
  }
}
