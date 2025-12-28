package org.emulinker.kaillera.protocol.netty.v086

import io.netty.buffer.ByteBuf
import org.emulinker.kaillera.protocol.v086.V086Message

sealed interface NettyMessageSerializer<T : V086Message> {
  val messageTypeId: Byte

  fun read(buffer: ByteBuf, messageNumber: Int, charset: String): T

  fun write(buffer: ByteBuf, message: T, charset: String)
}
