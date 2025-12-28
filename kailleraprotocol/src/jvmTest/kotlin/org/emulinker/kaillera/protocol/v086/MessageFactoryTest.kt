package org.emulinker.kaillera.protocol.v086

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MessageFactoryTest {

  @Test
  fun `all implementations of MessageSerializer are present in MessageFactory serializers array`() {
    val sealedSubclasses = MessageSerializer::class.sealedSubclasses
    val allSerializers = sealedSubclasses.mapNotNull { it.objectInstance }

    val registeredSerializers = MessageFactory.serializers.filterNotNull()

    assertThat(registeredSerializers).containsExactlyElementsIn(allSerializers)
  }

  @Test
  fun `serializer ID matches its index in the array`() {
    val serializers = MessageFactory.serializers
    for (i in serializers.indices) {
      val serializer = serializers[i]
      if (serializer != null) {
        assertThat(serializer.messageTypeId.toInt()).isEqualTo(i)
      }
    }
  }
}
