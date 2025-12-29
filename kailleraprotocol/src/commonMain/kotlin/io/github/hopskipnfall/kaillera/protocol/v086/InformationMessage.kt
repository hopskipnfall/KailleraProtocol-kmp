package io.github.hopskipnfall.kaillera.protocol.v086

import kotlinx.io.Sink
import kotlinx.io.Source

data class InformationMessage(
  override var messageNumber: Int,
  val source: String,
  val message: String,
) : V086Message(), ServerMessage {
  override val messageTypeId = ID

  override val bodyBytes: Int
    get() =
      StringUtil.encode(source, "ISO-8859-1").size +
        1 +
        StringUtil.encode(message, "ISO-8859-1").size +
        1

  override fun writeBodyTo(sink: Sink, charset: String) {
    InformationMessageSerializer.write(sink, this, charset)
  }

  companion object {
    const val ID: Byte = 0x17
  }

  object InformationMessageSerializer : MessageSerializer<InformationMessage> {
    override val messageTypeId: Byte = ID

    override fun read(source: Source, messageNumber: Int, charset: String): InformationMessage {
      val sourceStr = StringUtil.readString(source, charset)
      val message = StringUtil.readString(source, charset)
      return InformationMessage(messageNumber, sourceStr, message)
    }

    override fun write(sink: Sink, message: InformationMessage, charset: String) {
      StringUtil.writeString(sink, message.source, charset)
      StringUtil.writeString(sink, message.message, charset)
    }
  }
}
