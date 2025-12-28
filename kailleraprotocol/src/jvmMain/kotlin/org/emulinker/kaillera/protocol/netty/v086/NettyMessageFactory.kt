package org.emulinker.kaillera.protocol.netty.v086

import io.netty.buffer.ByteBuf
import org.emulinker.kaillera.protocol.v086.V086Message

object NettyMessageFactory {
  internal val serializers: Array<NettyMessageSerializer<*>?> =
    arrayOf(
      null, // 0x00
      NettyQuitSerializer, // 0x01
      NettyUserJoinedSerializer, // 0x02
      NettyUserInformationSerializer, // 0x03
      NettyServerStatusSerializer, // 0x04
      NettyServerAckSerializer, // 0x05
      NettyClientAckSerializer, // 0x06
      NettyChatSerializer, // 0x07
      NettyGameChatSerializer, // 0x08
      NettyKeepAliveSerializer, // 0x09
      NettyCreateGameSerializer, // 0x0A
      NettyQuitGameSerializer, // 0x0B
      NettyJoinGameSerializer, // 0x0C
      NettyPlayerInformationSerializer, // 0x0D
      NettyGameStatusSerializer, // 0x0E
      NettyGameKickSerializer, // 0x0F
      NettyCloseGameSerializer, // 0x10
      NettyStartGameSerializer, // 0x11
      NettyGameDataSerializer, // 0x12
      NettyCachedGameDataSerializer, // 0x13
      NettyPlayerDropSerializer, // 0x14
      NettyAllReadySerializer, // 0x15
      null, // 0x16
      NettyInformationMessageSerializer, // 0x17
      null, // 0x18
      NettyConnectionRejectedSerializer, // 0x19
    )

  fun read(
    messageNumber: Int,
    messageTypeId: Byte,
    buffer: ByteBuf,
    charset: String,
  ): V086Message? {
    val id = messageTypeId.toInt()
    val serializer = serializers[id]
    return serializer?.read(buffer, messageNumber, charset)
  }

  fun write(buffer: ByteBuf, message: V086Message, charset: String) {
    val id = message.messageTypeId.toInt()
    val serializer = serializers[id] as? NettyMessageSerializer<V086Message>
    serializer?.write(buffer, message, charset)
  }
}
