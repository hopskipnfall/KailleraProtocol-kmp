package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.StringUtil
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray

object ConnectMessageFactory {
  private const val CHARSET = "ISO-8859-1"

  fun read(source: Source): ConnectMessage? {
    if (source.exhausted()) return null
    val bytes = source.readByteArray()
    if (bytes.isEmpty()) return null

    val msg = StringUtil.decode(bytes, CHARSET)

    when {
      msg.startsWith(RequestPrivateKailleraPortResponse.ID) -> {
        // HELLOD...
        if (msg.length < RequestPrivateKailleraPortResponse.ID.length + 2) return null
        if (msg.last().code != 0) return null

        try {
          val portStr = msg.substring(RequestPrivateKailleraPortResponse.ID.length, msg.length - 1)
          val port = portStr.toInt()
          return RequestPrivateKailleraPortResponse(port)
        } catch (e: NumberFormatException) {
          return null
        }
      }

      msg.startsWith(RequestPrivateKailleraPortRequest.ID) -> {
        // HELLO...
        if (msg.length < RequestPrivateKailleraPortRequest.ID.length + 2) return null
        if (msg.last().code != 0) return null

        val protocol = msg.substring(RequestPrivateKailleraPortRequest.ID.length, msg.length - 1)
        return RequestPrivateKailleraPortRequest(protocol)
      }

      msg.startsWith(ConnectMessage_PING.ID) -> {
        // PING
        if (msg.length != 5) return null // PING + 00
        if (msg.last().code != 0) return null
        return ConnectMessage_PING
      }

      msg.startsWith(ConnectMessage_PONG.ID) -> {
        // PONG
        if (msg.length != 5) return null
        if (msg.last().code != 0) return null
        return ConnectMessage_PONG
      }

      msg.startsWith(ConnectMessage_ServerFull.ID) -> {
        // TOO
        if (msg.length != ConnectMessage_ServerFull.ID.length + 1) return null
        if (msg.last().code != 0) return null
        return ConnectMessage_ServerFull
      }

      else -> {
        return null
      }
    }
  }

  fun write(sink: Sink, message: ConnectMessage) {
    val content =
      when (message) {
        is RequestPrivateKailleraPortRequest -> {
          message.id + message.protocol
        }

        is RequestPrivateKailleraPortResponse -> {
          message.id + message.port.toString()
        }

        is ConnectMessage_PING,
        is ConnectMessage_PONG,
        is ConnectMessage_ServerFull -> {
          message.id
        }
      }
    val bytes = StringUtil.encode(content, CHARSET)
    sink.write(bytes)
    sink.writeByte(0)
  }
}
