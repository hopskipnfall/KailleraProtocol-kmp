package io.github.hopskipnfall.kaillera.protocol.benchmarks

import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyGameDataSerializer
import io.github.hopskipnfall.kaillera.protocol.v086.GameData as KmpGameData
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
open class GameDataBenchmark {

  // 100 bytes of game data
  private val contentBytes = ByteArray(100) { ((it % 255).toByte()) }

  // KMP objects
  private val kmpGameData = KmpGameData(123, contentBytes)
  private lateinit var nettyInputBuf: ByteBuf
  private lateinit var kmpInputBuffer: Buffer

  // Reusable buffers for serialization to avoid allocation overhead during benchmark if possible
  // properly resetting them is key.
  // For JMH, usually we just allocate fresh or reset per invocation.
  // Given the request asks for "serialization and deserialization times",
  // usually we want to include the buffer write cost.

  @Setup(Level.Trial)
  fun setup() {
    // Pre-fill buffers for Deserialization benchmarks

    // Netty Deserialize Setup
    nettyInputBuf = Unpooled.buffer()
    NettyGameDataSerializer.write(nettyInputBuf, kmpGameData, "UTF-8")

    // Kotlinx-io Deserialize Setup
    kmpInputBuffer = Buffer()
    KmpGameData.GameDataSerializer.write(kmpInputBuffer, kmpGameData, "ISO-8859-1")
  }

  @Benchmark
  fun nettySerialize(): ByteBuf {
    val buffer = Unpooled.buffer()
    NettyGameDataSerializer.write(buffer, kmpGameData, "UTF-8")
    return buffer
  }

  @Benchmark
  fun kotlinxIoSerialize(): Buffer {
    val buffer = Buffer()
    kmpGameData.writeBodyTo(buffer, "ISO-8859-1")
    return buffer
  }

  @Benchmark
  fun nettyDeserialize(): KmpGameData {
    val buf = nettyInputBuf.retainedSlice()
    buf.setIndex(0, buf.capacity())
    val result = NettyGameDataSerializer.read(buf, 123, "UTF-8")
    buf.release()
    return result
  }

  @Benchmark
  fun kotlinxIoDeserialize(): KmpGameData {
    // kotlinx.io Buffer is a bit different. We can't just "slice" and reset easily without copy if
    // we consume it.
    // We can use `copy()` which makes a deep copy of the buffer.
    val copy = kmpInputBuffer.copy()
    // KmpGameDataSerializer reads 0x00, short len, then bytes.
    val result = KmpGameData.GameDataSerializer.read(copy, 123, "ISO-8859-1")
    return result
  }
}
