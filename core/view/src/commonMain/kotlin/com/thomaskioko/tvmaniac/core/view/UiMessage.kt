package com.thomaskioko.tvmaniac.core.view

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.uuid.Uuid

data class UiMessage(
    val message: String,
    val id: Long = Uuid.random().getMostSignificantBitsFromBytes(),
)

fun UiMessage(
    t: Throwable,
    id: Long = Uuid.random().getMostSignificantBitsFromBytes(),
): UiMessage = UiMessage(
  message = t.message ?: "Error occurred: $t",
  id = id,
)

internal fun Uuid.getMostSignificantBitsFromBytes(): Long {
  val bytes = this.toByteArray()
  return bytes.sliceArray(0..7).fold(0L) { acc, byte ->
    (acc shl 8) or (byte.toLong() and 0xFF)
  }
}


class UiMessageManager {
    private val mutex = Mutex()

    private val _messages = MutableStateFlow(emptyList<UiMessage>())

    val message: Flow<UiMessage?> = _messages.map { it.firstOrNull() }.distinctUntilChanged()

    suspend fun emitMessage(message: UiMessage) {
        mutex.withLock {
            _messages.value = _messages.value + message
        }
    }

    suspend fun clearMessage(id: Long) {
        mutex.withLock {
            _messages.value = _messages.value.filterNot { it.id == id }
        }
    }

}


