package org.emulinker.kaillera.protocol.connection

sealed interface ConnectMessage {
  val id: String

  // Optional: We can define write here or keep it in serializer.
  // Given these are simple text messages, putting write logic here is fine for KMP simplicity.
  // But aligning with V086Message, maybe just data classes?
  // V086Message has `writeBodyTo`.
  // These messages are weird text-based ones.
}
