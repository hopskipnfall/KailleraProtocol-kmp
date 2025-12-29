package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readIntLe
import kotlinx.io.writeIntLe

sealed class Ack : V086Message() {
  override val bodyBytes: Int = 17 // 1 (0x00) + 4 * 4 (Ints)
}

data class ClientAck(override var messageNumber: Int) : Ack(), ClientMessage {
  override val messageTypeId = ID

  override fun writeBodyTo(sink: Sink, charset: String) {
    ClientAckSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x06
  }

  object ClientAckSerializer : MessageSerializer<ClientAck> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): ClientAck {
      source.readByte() // 0x00
      source.readIntLe()
      source.readIntLe()
      source.readIntLe()
      source.readIntLe()
      return ClientAck(messageNumber)
    }

    override fun write(sink: Sink, message: ClientAck, charset: String) {
      sink.writeByte(0x00)
      sink.writeIntLe(0)
      sink.writeIntLe(1)
      sink.writeIntLe(2)
      sink.writeIntLe(3)
    }
  }
}

data class ServerAck(
  override var messageNumber: Int,
  val val1: Int = 0,
  val val2: Int = 1,
  val val3: Int = 2,
  val val4: Int = 3,
) : Ack(), ServerMessage {
  override val messageTypeId = ID

  override fun writeBodyTo(sink: Sink, charset: String) {
    ServerAckSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x05
  }

  object ServerAckSerializer : MessageSerializer<ServerAck> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): ServerAck {
      source.readByte() // 0x00
      val val1 = source.readIntLe()
      val val2 = source.readIntLe()
      val val3 = source.readIntLe()
      val val4 = source.readIntLe()
      return ServerAck(messageNumber, val1, val2, val3, val4)
    }

    override fun write(sink: Sink, message: ServerAck, charset: String) {
      sink.writeByte(0x00)
      sink.writeIntLe(message.val1)
      sink.writeIntLe(message.val2)
      sink.writeIntLe(message.val3)
      sink.writeIntLe(message.val4)
    }
  }
}
