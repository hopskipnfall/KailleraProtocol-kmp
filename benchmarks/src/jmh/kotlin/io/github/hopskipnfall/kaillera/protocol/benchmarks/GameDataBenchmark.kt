package io.github.hopskipnfall.kaillera.protocol.benchmarks

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyMessageFactory
import io.github.hopskipnfall.kaillera.protocol.v086.GameData as KmpGameData
import io.github.hopskipnfall.kaillera.protocol.v086.MessageFactory
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.concurrent.TimeUnit
import kotlinx.io.Buffer
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.infra.Blackhole

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class GameDataBenchmark {
  // 24 bytes of game data
  private val contentBytes = ByteArray(24) { ((it % 255).toByte()) }

  private val kmpGameData = KmpGameData(123, contentBytes)

  private lateinit var nettyDeserializeBuffer: ByteBuf
  private lateinit var kotlinxIoDeserializeBuffer: Buffer

  @Setup(Level.Trial)
  fun setup() {
    nettyDeserializeBuffer = Unpooled.buffer(4096)
    kotlinxIoDeserializeBuffer = Buffer()

    NettyMessageFactory.write(nettyDeserializeBuffer, kmpGameData, CHARSET)
    kmpGameData.writeBodyTo(kotlinxIoDeserializeBuffer, CHARSET.name())
  }

  @TearDown(Level.Trial)
  fun tearDown() {
    nettyDeserializeBuffer.release()
  }

  @Benchmark
  fun nettySerialize(blackhole: Blackhole) {
    val buf = Unpooled.buffer(kmpGameData.bodyBytes)

    NettyMessageFactory.write(buf, kmpGameData, CHARSET)
    blackhole.consume(buf)

    if (VALIDATE) {
      check(
        NettyMessageFactory.read(kmpGameData.messageNumber, KmpGameData.ID, buf, CHARSET) ==
          kmpGameData
      )
    }
    buf.release()
  }

  @Benchmark
  fun kotlinxIoSerialize(blackhole: Blackhole) {
    val buffer = Buffer()

    kmpGameData.writeBodyTo(buffer, CHARSET.name())
    blackhole.consume(buffer)

    if (VALIDATE) {
      check(
        MessageFactory.read(buffer, kmpGameData.messageNumber, KmpGameData.ID, CHARSET.name()) ==
          kmpGameData
      )
    }
  }

  @Benchmark
  fun nettyDeserialize(blackhole: Blackhole) {
    val buf = nettyDeserializeBuffer.slice()

    val result = NettyMessageFactory.read(kmpGameData.messageNumber, KmpGameData.ID, buf, CHARSET)
    blackhole.consume(result)

    if (VALIDATE) check(result == kmpGameData)
  }

  @Benchmark
  fun kotlinxIoDeserialize(blackhole: Blackhole) {
    val buffer = kotlinxIoDeserializeBuffer.copy()

    val result = MessageFactory.read(buffer, 123, KmpGameData.ID, "ISO-8859-1")
    blackhole.consume(result)

    if (VALIDATE) check(result == kmpGameData)
  }

  private companion object {
    /**
     * Whether the benchmarks should validate if the outcome was correct.
     *
     * Disabled by default so it doesn't add to the benchmark time.
     */
    const val VALIDATE = false

    val CHARSET = Charsets.ISO_8859_1
  }
}
