package com.thomaskioko.tvmaniac.core.view

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.uuid.Uuid

public data class UiMessage(
    val message: String,
    val id: Long = Uuid.random().getMostSignificantBitsFromBytes(),
    val sourceId: String? = null,
)

public fun UiMessage(
    t: Throwable,
    id: Long = Uuid.random().getMostSignificantBitsFromBytes(),
    sourceId: String? = null,
): UiMessage = UiMessage(
    message = t.message ?: "Error occurred: $t",
    id = id,
    sourceId = sourceId,
)

internal fun Uuid.getMostSignificantBitsFromBytes(): Long {
    val bytes = this.toByteArray()
    return bytes.sliceArray(0..7).fold(0L) { acc, byte ->
        (acc shl 8) or (byte.toLong() and 0xFF)
    }
}

public class UiMessageManager {
    private val mutex = Mutex()

    private val _messages = MutableStateFlow(emptyList<UiMessage>())

    public val message: Flow<UiMessage?> = _messages.map { it.firstOrNull() }.distinctUntilChanged()

    public suspend fun emitMessageCombined(throwable: Throwable, sourceId: String? = null) {
        mutex.withLock {
            val errorMessage = throwable.message ?: "Error occurred: $throwable"

            // Check if we already have messages with the same error type
            val existingMessages = _messages.value.filter {
                it.message.contains(errorMessage) || errorMessage.contains(it.message)
            }

            if (existingMessages.isEmpty()) {
                _messages.value = _messages.value + UiMessage(throwable, sourceId = sourceId)
            } else {
                // We have similar messages, combine them
                val firstMessage = existingMessages.first()
                val sources = mutableSetOf<String>()

                // Collect all source IDs from existing messages
                existingMessages.forEach { message ->
                    if (message.sourceId != null) {
                        sources.add(message.sourceId)
                    }
                }

                if (sourceId != null) {
                    sources.add(sourceId)
                }

                val combinedMessage = if (sources.isEmpty()) {
                    "Multiple errors of type: $errorMessage"
                } else {
                    "Errors in ${sources.joinToString(", ")}: $errorMessage"
                }

                // We need to store the combined sources in a way that can be retrieved later
                // For now, we'll use the first source as a representative
                val representativeSource = sources.firstOrNull()

                // Replace the existing message with the combined one
                _messages.value = _messages.value.filterNot {
                    existingMessages.contains(it)
                } + UiMessage(combinedMessage, firstMessage.id, sourceId = representativeSource)
            }
        }
    }

    public suspend fun clearMessage(id: Long) {
        mutex.withLock {
            _messages.value = _messages.value.filterNot { it.id == id }
        }
    }
}
