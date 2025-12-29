package io.github.hopskipnfall.kaillera.protocol.netty.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.model.GameStatus
import io.github.hopskipnfall.kaillera.protocol.v086.AllReady
import io.github.hopskipnfall.kaillera.protocol.v086.CachedGameData
import io.github.hopskipnfall.kaillera.protocol.v086.CloseGame
import io.github.hopskipnfall.kaillera.protocol.v086.GameKick
import io.github.hopskipnfall.kaillera.protocol.v086.MessageSerializer
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDrop
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropNotification
import io.github.hopskipnfall.kaillera.protocol.v086.PlayerDropRequest
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGame
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGameNotification
import io.github.hopskipnfall.kaillera.protocol.v086.QuitGameRequest
import io.github.hopskipnfall.kaillera.protocol.v086.TestUtils.hexStringToByteArray
import io.github.hopskipnfall.kaillera.protocol.v086.V086Message
import io.netty.buffer.Unpooled
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import org.junit.Test

class SimpleMessageTests2 {

  private fun <T : V086Message> testKmp(
    serializer: MessageSerializer<T>,
    message: T,
    hexString: String,
  ) {
    val buffer = Buffer()
    serializer.write(buffer, message, "ISO-8859-1")
    val writtenBytes = buffer.readByteArray()
    val expectedBytes = hexStringToByteArray(hexString)
    if (!writtenBytes.contentEquals(expectedBytes)) {
      println(
        "KMP Write Mismatch: Expected ${expectedBytes.contentToString()}, Actual ${writtenBytes.contentToString()}"
      )
    }
    assertThat(writtenBytes).isEqualTo(expectedBytes)

    val readBuffer = Buffer()
    readBuffer.write(expectedBytes)
    val readMessage = serializer.read(readBuffer, message.messageNumber, "ISO-8859-1")
    if (readMessage != message) {
      println("KMP Read Mismatch: Expected $message, Actual $readMessage")
    }
    assertThat(readMessage).isEqualTo(message)
  }

  private fun <T : V086Message> testNetty(
    readFunc: (io.netty.buffer.ByteBuf, Int, String) -> T,
    writeFunc: (io.netty.buffer.ByteBuf, T, String) -> Unit,
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
  fun quitGameRequest() {
    val msg = QuitGameRequest(42)
    val hex = "00, FF, FF"
    testKmp(QuitGame.QuitGameSerializer, msg, hex)
    testNetty(NettyQuitGameSerializer::read, NettyQuitGameSerializer::write, msg, hex)
  }

  @Test
  fun quitGameNotification() {
    val msg = QuitGameNotification(42, "nue", 13)
    val hex = "6E, 75, 65, 00, 0D, 00"
    testKmp(QuitGame.QuitGameSerializer, msg, hex)
    testNetty(NettyQuitGameSerializer::read, NettyQuitGameSerializer::write, msg, hex)
  }

  @Test
  fun playerDropRequest() {
    val msg = PlayerDropRequest(42)
    val hex = "00, 00"
    testKmp(PlayerDrop.PlayerDropSerializer, msg, hex)
    testNetty(NettyPlayerDropSerializer::read, NettyPlayerDropSerializer::write, msg, hex)
  }

  @Test
  fun playerDropNotification() {
    val msg = PlayerDropNotification(42, "nue", 100)
    val hex = "6E, 75, 65, 00, 64"
    testKmp(PlayerDrop.PlayerDropSerializer, msg, hex)
    testNetty(NettyPlayerDropSerializer::read, NettyPlayerDropSerializer::write, msg, hex)
  }

  @Test
  fun allReady() {
    val msg = AllReady(42)
    val hex = "00"
    testKmp(AllReady.AllReadySerializer, msg, hex)
    testNetty(NettyAllReadySerializer::read, NettyAllReadySerializer::write, msg, hex)
  }

  @Test
  fun cachedGameData() {
    val msg = CachedGameData(42, 12)
    val hex = "00, 0C"
    testKmp(CachedGameData.CachedGameDataSerializer, msg, hex)
    testNetty(NettyCachedGameDataSerializer::read, NettyCachedGameDataSerializer::write, msg, hex)
  }

  @Test
  fun gameKick() {
    val msg = GameKick(42, 13)
    val hex = "00, 0D, 00"
    testKmp(GameKick.GameKickSerializer, msg, hex)
    testNetty(NettyGameKickSerializer::read, NettyGameKickSerializer::write, msg, hex)
  }

  @Test
  fun closeGame() {
    val msg = CloseGame(42, 10, 999)
    val hex = "00, 0A, 00, E7, 03"
    testKmp(CloseGame.CloseGameSerializer, msg, hex)
    testNetty(NettyCloseGameSerializer::read, NettyCloseGameSerializer::write, msg, hex)
  }

  @Test
  fun gameStatus() {
    val msg =
      io.github.hopskipnfall.kaillera.protocol.v086.GameStatus(
        messageNumber = 42,
        gameId = 13,
        val1 = 2345,
        gameStatus = GameStatus.SYNCHRONIZING,
        numPlayers = 4,
        maxPlayers = 4,
      )
    val hex = "00, 0D, 00, 29, 09, 01, 04, 04"
    testKmp(io.github.hopskipnfall.kaillera.protocol.v086.GameStatus.GameStatusSerializer, msg, hex)
    testNetty(NettyGameStatusSerializer::read, NettyGameStatusSerializer::write, msg, hex)
  }
}
