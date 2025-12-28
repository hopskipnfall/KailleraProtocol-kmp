# KailleraProtocol-kmp

This is a Kotlin Multiplatform library for efficiently working with Kaillera protocol messages accurately across various languages. It is used by the server [EmuLinker-K (elk)](https://github.com/hopskipnfall/EmuLinker-K).

## V086Bundle and GameData

The library provides structures to handle Kaillera protocol messages (v0.86). For example:

## Usage Examples

### Kotlin (Common)

This logic is shared across all platforms (JVM, JS, Native).

```kotlin
import org.emulinker.kaillera.protocol.v086.GameData
import org.emulinker.kaillera.protocol.v086.V086Bundle
import org.emulinker.kaillera.protocol.v086.V086BundleSerializer
import kotlinx.io.Buffer

fun main() {
    // 1. Create the GameData message
    // messageNumber: A sequence number for ordering (e.g., 100)
    // gameData: The actual input data as a ByteArray
    val inputPayload = byteArrayOf(0x01, 0x02)
    val gameData = GameData(messageNumber = 100, gameData = inputPayload)

    // 2. Wrap the message in a V086Bundle
    val bundle = V086Bundle.Single(gameData)

    // 3. Serialize to bytes using V086BundleSerializer
    val buffer = Buffer()
    V086BundleSerializer.write(buffer, bundle, charset = "ISO-8859-1")
    
    val bytes = buffer.readByteArray()
    println("Serialized bytes: ${bytes.joinToString(",") { "%02x".format(it) }}")
}
```

### Kotlin (JVM / Netty)

On the JVM, you can also use `NettyV086BundleSerializer` to work directly with Netty `ByteBuf`.

```kotlin
import org.emulinker.kaillera.protocol.netty.v086.NettyV086BundleSerializer
import io.netty.buffer.Unpooled

// ... assuming 'bundle' is created as above ...

val nettyBuffer = Unpooled.buffer()
NettyV086BundleSerializer.write(nettyBuffer, bundle, "ISO-8859-1")
```

### TypeScript

```typescript
// Example usage in a TypeScript environment

// 1. Create the input data (Int8Array corresponds to Kotlin's ByteArray)
const inputPayload = new Int8Array([0x01, 0x02]);

// 2. Create the GameData message
const gameData = new org.emulinker.kaillera.protocol.v086.GameData(100, inputPayload);

// 3. Wrap it in a V086Bundle
const bundle = new org.emulinker.kaillera.protocol.v086.V086Bundle.Single(gameData);

console.log("Bundle created:", bundle);
```
