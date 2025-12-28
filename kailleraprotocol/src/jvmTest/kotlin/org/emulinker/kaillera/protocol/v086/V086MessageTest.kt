package org.emulinker.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import kotlinx.io.Buffer
import org.emulinker.kaillera.protocol.v086.TestUtils.assertBufferContainsExactly
import org.emulinker.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import org.junit.Test

abstract class V086MessageTest<K : V086Message> {
  abstract val message: K
  abstract val byteString: String
  abstract val serializer: MessageSerializer<K>

  @Test
  fun bodyLength() {
    val bytes = hexStringToByteArray(byteString)
    // message.bodyBytes is strictly body without ID.
    // But message logic says BodyBytes + 1 (ID) = Total Length.
    // Wait, review V086Message.kt implementation.
    // bodyBytes is excluding message ID.
    // bodyBytesPlusMessageIdType is including message ID.
    // The byteString in tests usually includes the whole packet or just the body?
    // Let's check original ProtocolBaseTest.

    // Original:
    // val byteBuffer = V086Utils.hexStringToByteBuffer(byteString)
    // assertThat(message.bodyBytes).isEqualTo(byteBuffer.remaining())

    // This suggests byteString IS the body only?
    // Let's check GameDataTest.kt.
    // byteString = "00, 05, 00, 02, 03, 04, 05, 06"
    // GameData logic: 0x00 + Short(Size) + Data.
    // 1 + 2 + 5 = 8 bytes.
    // The string above has 8 bytes.
    // So byteString IS the body (excluding header like msgNum, length, msgID? No wait).
    // V086Message.writeTo writes: Number (2), Len (2), ID (1), Body.
    // writeBodyTo writes: Body.
    // GameData.writeBodyTo writes: 0x00, Size(2), Data.
    // So byteString corresponds to `writeBodyTo` output.

    assertThat(message.bodyBytes).isEqualTo(bytes.size)
  }

  @Test
  fun read() {
    val buffer = Buffer()
    buffer.write(hexStringToByteArray(byteString))

    // The serializer.read expects the buffer to start at the body (or ID? No).
    // Protocol logic: read header, then switch on ID, then call serializer.read(buffer, num).
    // The serializer.read typically reads the body.
    // In GameDataSerializer.read: source.readByte() // Skip 0x00 ...
    // So indeed `serializer.read` consumes the body.

    val deserialized = serializer.read(buffer, message.messageNumber, "ISO-8859-1")

    assertThat(deserialized).isEqualTo(message)
    assertThat(buffer.exhausted()).isTrue()
  }

  @Test
  fun write() {
    val buffer = Buffer()
    message.writeBodyTo(buffer, "ISO-8859-1")

    assertThat(buffer.size).isEqualTo(message.bodyBytes.toLong())
    assertBufferContainsExactly(buffer, byteString)
  }
}
