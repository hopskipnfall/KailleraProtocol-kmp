package io.github.hopskipnfall.kaillera.protocol.netty.v086

import io.github.hopskipnfall.kaillera.protocol.v086.V086Message
import io.netty.buffer.ByteBuf

sealed interface NettyMessageSerializer<T : V086Message> {
  val messageTypeId: Byte

  fun read(buffer: ByteBuf, messageNumber: Int, charset: String): T

  fun write(buffer: ByteBuf, message: T, charset: String)
}
