package io.github.hopskipnfall.kaillera.protocol.v086

import kotlin.jvm.JvmInline

sealed interface V086Bundle {
  val messages: List<V086Message>

  @JvmInline
  value class Single(val message: V086Message) : V086Bundle {
    override val messages: List<V086Message>
      get() = listOf(message)
  }

  @JvmInline value class Multi(override val messages: List<V086Message>) : V086Bundle
}
