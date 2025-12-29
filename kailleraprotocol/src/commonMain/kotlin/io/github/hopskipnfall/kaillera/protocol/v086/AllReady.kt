package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

data class AllReady(override var messageNumber: Int) : V086Message(), ServerMessage, ClientMessage {
  override val messageTypeId = ID
  override val bodyBytes: Int = 1

  override fun writeBodyTo(sink: Sink, charset: String) {
    AllReadySerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x15
  }

  object AllReadySerializer : MessageSerializer<AllReady> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): AllReady {
      source.readByte() // 0x00
      return AllReady(messageNumber)
    }

    override fun write(sink: Sink, message: AllReady, charset: String) {
      sink.writeByte(0x00)
    }
  }
}
