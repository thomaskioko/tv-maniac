package com.thomaskioko.tvmaniac.data.backup.api

import com.thomaskioko.tvmaniac.data.backup.api.model.ShowRefillProgress
import kotlinx.coroutines.flow.StateFlow

public interface ShowRefillReporter {
    public val progress: StateFlow<ShowRefillProgress>

    public fun begin(total: Int)

    public fun advance()

    public fun clear()
}
