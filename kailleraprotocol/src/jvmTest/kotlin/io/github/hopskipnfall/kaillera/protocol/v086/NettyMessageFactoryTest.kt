package io.github.hopskipnfall.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyMessageFactory
import io.github.hopskipnfall.kaillera.protocol.netty.v086.NettyMessageSerializer
import org.junit.Test

class NettyMessageFactoryTest {

  @Test
  fun `all implementations of NettyMessageSerializer are present in NettyMessageFactory serializers array`() {
    val sealedSubclasses = NettyMessageSerializer::class.sealedSubclasses
    val allSerializers = sealedSubclasses.mapNotNull { it.objectInstance }

    val registeredSerializers = NettyMessageFactory.serializers.filterNotNull()

    assertThat(registeredSerializers).containsExactlyElementsIn(allSerializers)
  }

  @Test
  fun `serializer ID matches its index in the array`() {
    val serializers = NettyMessageFactory.serializers
    for (i in serializers.indices) {
      val serializer = serializers[i]
      if (serializer != null) {
        assertThat(serializer.messageTypeId.toInt()).isEqualTo(i)
      }
    }
  }
}
