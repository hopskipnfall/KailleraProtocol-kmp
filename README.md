# KailleraProtocol-kmp

This is a Kotlin Multiplatform library for efficiently working with Kaillera protocol messages accurately across various languages.


## Supported Targets

- **JVM** (Java, Kotlin)
- **Javascript** (Browser, Node.js)
- **Native**:
  - Linux (x64)
  - Windows (MinGW x64)
  - macOS (x64, arm64)
  - iOS (x64, arm64, Simulator arm64)


### Supported Encodings

The library provides cross-platform support for the following encodings. Note that `ISO-8859-1` is explicitly optimized and supported on all targets as it is the most common.

| Encoding                     | JVM | Native (Unix/Win)                       | JS                |
|------------------------------|-----|-----------------------------------------|-------------------|
| **ISO-8859-1** / **Latin-1** | ✅   | ✅ (Manual)                              | ✅ (Manual)        |
| **UTF-8**                    | ✅   | ✅                                       | ✅                 |
| **Shift_JIS** (Japanese)     | ✅   | Partial (Available if host supports it) | Browser dependent |
| **Windows-1251** (Cyrillic)  | ✅   | Partial (Available if host supports it) | Browser dependent |
| **EUC-KR** (Korean)          | ✅   | Partial (Available if host supports it) | Browser dependent |

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

### Swift (iOS / macOS)

The library compiles to an Objective-C framework that can be used from Swift.

```swift
import KailleraProtocol // Or the name of the framework you generate

func example() {
    // 1. Create the input data
    // In Swift, [Int8] maps to KotlinByteArray
    let inputPayload: [Int8] = [0x01, 0x02]
    
    // KotlinByteArray handling might vary slightly depending on KMP version/export.
    // Often you need to convert Swift array to KotlinByteArray if the API demands it explicitly,
    // or sometimes it maps automatically. 
    // For specific interop, you might use a helper function or direct instantiation.
    // Assuming standard KMP mapping:
    let kotlinByteArray = KotlinByteArray(size: Int32(inputPayload.count))
    for (index, byte) in inputPayload.enumerated() {
        kotlinByteArray.set(index: Int32(index), value: byte)
    }

    // 2. Create the GameData message
    let gameData = GameData(messageNumber: 100, gameData: kotlinByteArray)

    // 3. Wrap it in a V086Bundle
    // Note: Sealed classes in KMP often map to specific subclasses in ObjC/Swift
    let bundle = V086BundleSingle(value: gameData)

    // 4. Serialize
    print("Bundle created: \(bundle)")
}
```
