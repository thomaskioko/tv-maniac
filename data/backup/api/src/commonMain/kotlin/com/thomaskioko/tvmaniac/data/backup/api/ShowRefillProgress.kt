package com.thomaskioko.tvmaniac.data.backup.api

import kotlinx.coroutines.flow.StateFlow

public data class ShowRefillProgress(
    val completed: Int = 0,
    val total: Int = 0,
)

public interface ShowRefillReporter {
    public val progress: StateFlow<ShowRefillProgress>

    public fun begin(total: Int)

    public fun advance()

    public fun clear()
}
