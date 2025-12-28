package org.emulinker.kaillera.protocol.netty.connection

import io.netty.buffer.ByteBuf
import java.nio.charset.Charset
import org.emulinker.kaillera.protocol.connection.ConnectMessage
import org.emulinker.kaillera.protocol.connection.ConnectMessage_PING
import org.emulinker.kaillera.protocol.connection.ConnectMessage_PONG
import org.emulinker.kaillera.protocol.connection.ConnectMessage_ServerFull
import org.emulinker.kaillera.protocol.connection.RequestPrivateKailleraPortRequest
import org.emulinker.kaillera.protocol.connection.RequestPrivateKailleraPortResponse

object NettyConnectMessageFactory {
  private val CHARSET = Charset.forName("ISO-8859-1")

  fun read(buffer: ByteBuf): ConnectMessage? {
    // These messages are small strings, read the whole thing?
    // Legacy ConnectMessage.parse reads buffer.readableBytes()
    // But what if multiple messages in buffer?
    // Connect protocol seems to be one message per packet usually?
    // "HELLO" handshake is UDP? Or TCP?
    // emulinker ConnectController uses UDP?
    // parse(buffer) in ConnectMessage reads all bytes.

    val length = buffer.readableBytes()
    if (length == 0) return null

    val msg = buffer.readCharSequence(length, CHARSET).toString()
    // Check if it has a null terminator at end?
    // Legacy checks: msg.last().code != 0x00
    // toString() will include the 0x00 if it's there.

    // Let's implement logic based on string content
    // Check Response first because "HELLOD00D" starts with "HELLO"
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
        // Check Length etc?
        // Legacy: length < ID.length + 2 exception. msg.last() == 0x00
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

  fun write(buffer: ByteBuf, message: ConnectMessage) {
    when (message) {
      is RequestPrivateKailleraPortRequest -> {
        buffer.writeCharSequence(RequestPrivateKailleraPortRequest.ID, CHARSET)
        buffer.writeCharSequence(message.protocol, CHARSET)
      }

      is RequestPrivateKailleraPortResponse -> {
        buffer.writeCharSequence(RequestPrivateKailleraPortResponse.ID, CHARSET)
        buffer.writeCharSequence(message.port.toString(), CHARSET)
      }

      is ConnectMessage_PING -> {
        buffer.writeCharSequence(ConnectMessage_PING.ID, CHARSET)
      }

      is ConnectMessage_PONG -> {
        buffer.writeCharSequence(ConnectMessage_PONG.ID, CHARSET)
      }

      is ConnectMessage_ServerFull -> {
        buffer.writeCharSequence(ConnectMessage_ServerFull.ID, CHARSET)
      }
    }
    buffer.writeByte(0)
  }
}
