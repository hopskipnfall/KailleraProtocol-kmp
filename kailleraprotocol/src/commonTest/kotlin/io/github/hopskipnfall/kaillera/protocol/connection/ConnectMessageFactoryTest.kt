package io.github.hopskipnfall.kaillera.protocol.connection

import io.github.hopskipnfall.kaillera.protocol.v086.StringUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.io.Buffer

class ConnectMessageFactoryTest {

  @Test
  fun testPing() {
    val message = ConnectMessage_PING
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    val readMessage = ConnectMessageFactory.read(buffer)
    assertIs<ConnectMessage_PING>(readMessage)
  }

  @Test
  fun testPong() {
    val message = ConnectMessage_PONG
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    val readMessage = ConnectMessageFactory.read(buffer)
    assertIs<ConnectMessage_PONG>(readMessage)
  }

  @Test
  fun testServerFull() {
    val message = ConnectMessage_ServerFull
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    val readMessage = ConnectMessageFactory.read(buffer)
    assertIs<ConnectMessage_ServerFull>(readMessage)
  }

  @Test
  fun testRequestPrivateKailleraPortRequest() {
    val protocol = "0.86"
    val message = RequestPrivateKailleraPortRequest(protocol)
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    val readMessage = ConnectMessageFactory.read(buffer)
    assertIs<RequestPrivateKailleraPortRequest>(readMessage)
    assertEquals(protocol, readMessage.protocol)
  }

  @Test
  fun testRequestPrivateKailleraPortResponse() {
    val port = 27888
    val message = RequestPrivateKailleraPortResponse(port)
    val buffer = Buffer()
    ConnectMessageFactory.write(buffer, message)

    val readMessage = ConnectMessageFactory.read(buffer)
    assertIs<RequestPrivateKailleraPortResponse>(readMessage)
    assertEquals(port, readMessage.port)
  }

  @Test
  fun testReadGarbage() {
    val buffer = Buffer()
    buffer.write(StringUtil.encode("GARBAGE\u0000", "ISO-8859-1"))
    val readMessage = ConnectMessageFactory.read(buffer)
    assertNull(readMessage)
  }

  @Test
  fun testReadNoNullTerminator() {
    val buffer = Buffer()
    buffer.write(StringUtil.encode("PING", "ISO-8859-1")) // No \0
    val readMessage = ConnectMessageFactory.read(buffer)
    assertNull(readMessage)
  }
}
