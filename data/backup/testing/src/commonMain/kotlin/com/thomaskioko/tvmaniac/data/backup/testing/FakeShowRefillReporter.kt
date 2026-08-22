package com.thomaskioko.tvmaniac.data.backup.testing

import com.thomaskioko.tvmaniac.data.backup.api.ShowRefillReporter
import com.thomaskioko.tvmaniac.data.backup.api.model.ShowRefillProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public class FakeShowRefillReporter : ShowRefillReporter {

    private val mutableProgress = MutableStateFlow(ShowRefillProgress())

    public var beganWith: Int? = null
        private set

    public var advanceCount: Int = 0
        private set

    public var cleared: Boolean = false
        private set

    override val progress: StateFlow<ShowRefillProgress> = mutableProgress.asStateFlow()

    override fun begin(total: Int) {
        beganWith = total
        mutableProgress.value = ShowRefillProgress(completed = 0, total = total)
    }

    override fun advance() {
        advanceCount++
        mutableProgress.update { it.copy(completed = it.completed + 1) }
    }

    override fun clear() {
        cleared = true
        mutableProgress.value = ShowRefillProgress()
    }
}
