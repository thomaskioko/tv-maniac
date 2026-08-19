package com.thomaskioko.tvmaniac.data.backup.implementation

import com.thomaskioko.tvmaniac.data.backup.api.ShowRefillProgress
import com.thomaskioko.tvmaniac.data.backup.api.ShowRefillReporter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultShowRefillReporter : ShowRefillReporter {

    private val mutableProgress = MutableStateFlow(ShowRefillProgress())

    override val progress: StateFlow<ShowRefillProgress> = mutableProgress.asStateFlow()

    override fun begin(total: Int) {
        mutableProgress.value = ShowRefillProgress(completed = 0, total = total)
    }

    override fun advance() {
        mutableProgress.update { it.copy(completed = it.completed + 1) }
    }

    override fun clear() {
        mutableProgress.value = ShowRefillProgress()
    }
}
