package com.thomaskioko.tvmaniac.core.base.extensions

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transformLatest
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

public fun Flow<Boolean>.minTrueDuration(
    min: Duration,
    timeSource: TimeSource = TimeSource.Monotonic,
): Flow<Boolean> {
    val source = this
    return flow {
        var trueAt: TimeMark? = null
        source.transformLatest { value ->
            if (value) {
                if (trueAt == null) {
                    trueAt = timeSource.markNow()
                    emit(true)
                }
            } else {
                val mark = trueAt
                if (mark == null) {
                    emit(false)
                } else {
                    val elapsed = mark.elapsedNow()
                    if (elapsed < min) {
                        delay(min - elapsed)
                    }
                    trueAt = null
                    emit(false)
                }
            }
        }.collect { emit(it) }
    }
}
