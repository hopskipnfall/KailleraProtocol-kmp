package io.github.hopskipnfall.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.model.ConnectionType
import io.github.hopskipnfall.kaillera.protocol.model.GameStatus
import io.github.hopskipnfall.kaillera.protocol.model.UserStatus
import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyPlayerInformationSerializer
import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyServerStatusSerializer
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.junit.Test

class ComplexMessageTests2 {

  private fun <T : V086Message> testKmp(
    serializer: MessageSerializer<T>,
    message: T,
    hexString: String,
  ) {
    val buffer = Buffer()
    serializer.write(buffer, message, "ISO-8859-1")
    val writtenBytes = buffer.readByteArray()
    val expectedBytes = hexStringToByteArray(hexString)
    assertThat(writtenBytes).isEqualTo(expectedBytes)

    val readBuffer = Buffer()
    readBuffer.write(expectedBytes)
    val readMessage = serializer.read(readBuffer, message.messageNumber, "ISO-8859-1")
    assertThat(readMessage).isEqualTo(message)
  }

  private fun <T : V086Message> testNetty(
    readFunc: (ByteBuf, Int, String) -> T,
    writeFunc: (ByteBuf, T, String) -> Unit,
    message: T,
    hexString: String,
  ) {
    val buffer = Unpooled.buffer()
    writeFunc(buffer, message, "ISO-8859-1")
    val writtenBytes = ByteArray(buffer.readableBytes())
    buffer.readBytes(writtenBytes)
    val expectedBytes = hexStringToByteArray(hexString)
    assertThat(writtenBytes).isEqualTo(expectedBytes)

    val readBuffer = Unpooled.wrappedBuffer(expectedBytes)
    val readMessage = readFunc(readBuffer, message.messageNumber, "ISO-8859-1")
    assertThat(readMessage).isEqualTo(message)
  }

  @Test
  fun playerInformation() {
    val msg =
      PlayerInformation(
        42,
        listOf(
          PlayerInformation.Player("nue", 100.milliseconds, 13, ConnectionType.LAN),
          PlayerInformation.Player("nue1", 100.milliseconds, 14, ConnectionType.LAN),
          PlayerInformation.Player("nue2", 100.milliseconds, 18, ConnectionType.AVERAGE),
          PlayerInformation.Player("nue3", 100.milliseconds, 200, ConnectionType.LAN),
          PlayerInformation.Player("nue4", 100.milliseconds, 12, ConnectionType.LAN),
          PlayerInformation.Player("nue5", 100.milliseconds, 8, ConnectionType.BAD),
          PlayerInformation.Player("nue6", 100.milliseconds, 3, ConnectionType.BAD),
        ),
      )
    val hex =
      "00, 07, 00, 00, 00, 6E, 75, 65, 00, 64, 00, 00, 00, 0D, 00, 01, 6E, 75, 65, 31, 00, 64, 00, 00, 00, 0E, 00, 01, 6E, 75, 65, 32, 00, 64, 00, 00, 00, 12, 00, 04, 6E, 75, 65, 33, 00, 64, 00, 00, 00, C8, 00, 01, 6E, 75, 65, 34, 00, 64, 00, 00, 00, 0C, 00, 01, 6E, 75, 65, 35, 00, 64, 00, 00, 00, 08, 00, 06, 6E, 75, 65, 36, 00, 64, 00, 00, 00, 03, 00, 06"
    testKmp(PlayerInformation.PlayerInformationSerializer, msg, hex)
    testNetty(
      NettyPlayerInformationSerializer::read,
      NettyPlayerInformationSerializer::write,
      msg,
      hex,
    )
  }

  @Test
  fun serverStatus() {
    val msg =
      ServerStatus(
        42,
        users =
          listOf(
            ServerStatus.User(
              "nue",
              100.milliseconds,
              UserStatus.CONNECTING,
              13,
              ConnectionType.LAN,
            ),
            ServerStatus.User("nue1", 100.milliseconds, UserStatus.IDLE, 14, ConnectionType.LAN),
            ServerStatus.User(
              "nue2",
              100.milliseconds,
              UserStatus.PLAYING,
              18,
              ConnectionType.AVERAGE,
            ),
            ServerStatus.User(
              "nue3",
              100.milliseconds,
              UserStatus.CONNECTING,
              200,
              ConnectionType.LAN,
            ),
            ServerStatus.User("nue4", 100.milliseconds, UserStatus.PLAYING, 12, ConnectionType.LAN),
            ServerStatus.User(
              "nue5",
              100.milliseconds,
              UserStatus.CONNECTING,
              8,
              ConnectionType.BAD,
            ),
            ServerStatus.User("nue6", 100.milliseconds, UserStatus.IDLE, 3, ConnectionType.BAD),
          ),
        games =
          listOf(
            ServerStatus.Game("My ROM", 100, "My N64 Emulator", "nue", "2/4", GameStatus.PLAYING),
            ServerStatus.Game("My ROM", 123, "My N64 Emulator", "nue2", "2/4", GameStatus.PLAYING),
            ServerStatus.Game(
              "My ROM",
              22,
              "My N64 Emulator",
              "nue3",
              "2/4",
              GameStatus.SYNCHRONIZING,
            ),
            ServerStatus.Game("My ROM", 5, "My N64 Emulator", "nue4", "2/4", GameStatus.WAITING),
          ),
      )
    val hex =
      "00, 07, 00, 00, 00, 04, 00, 00, 00, 6E, 75, 65, 00, 64, 00, 00, 00, 02, 0D, 00, 01, 6E, 75, 65, 31, 00, 64, 00, 00, 00, 01, 0E, 00, 01, 6E, 75, 65, 32, 00, 64, 00, 00, 00, 00, 12, 00, 04, 6E, 75, 65, 33, 00, 64, 00, 00, 00, 02, C8, 00, 01, 6E, 75, 65, 34, 00, 64, 00, 00, 00, 00, 0C, 00, 01, 6E, 75, 65, 35, 00, 64, 00, 00, 00, 02, 08, 00, 06, 6E, 75, 65, 36, 00, 64, 00, 00, 00, 01, 03, 00, 06, 4D, 79, 20, 52, 4F, 4D, 00, 64, 00, 00, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 6E, 75, 65, 00, 32, 2F, 34, 00, 02, 4D, 79, 20, 52, 4F, 4D, 00, 7B, 00, 00, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 6E, 75, 65, 32, 00, 32, 2F, 34, 00, 02, 4D, 79, 20, 52, 4F, 4D, 00, 16, 00, 00, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 6E, 75, 65, 33, 00, 32, 2F, 34, 00, 01, 4D, 79, 20, 52, 4F, 4D, 00, 05, 00, 00, 00, 4D, 79, 20, 4E, 36, 34, 20, 45, 6D, 75, 6C, 61, 74, 6F, 72, 00, 6E, 75, 65, 34, 00, 32, 2F, 34, 00, 00"
    testKmp(ServerStatus.ServerStatusSerializer, msg, hex)
    testNetty(NettyServerStatusSerializer::read, NettyServerStatusSerializer::write, msg, hex)
  }
}
