package org.emulinker.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

data class KeepAlive(override var messageNumber: Int, val value: Int = 12) :
  V086Message(), ClientMessage {
  override val messageTypeId = ID
  override val bodyBytes: Int = 1

  override fun writeBodyTo(sink: Sink, charset: String) {
    KeepAliveSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x09
  }

  object KeepAliveSerializer : MessageSerializer<KeepAlive> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): KeepAlive {
      val value = source.readByte().toInt() and 0xFF
      return KeepAlive(messageNumber, value)
    }

    override fun write(sink: Sink, message: KeepAlive, charset: String) {
      sink.writeByte(message.value.toByte())
    }
  }
}
