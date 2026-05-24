package com.thomaskioko.tvmaniac.syncactivity.testing

import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.implementation.DefaultTraktActivityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultTraktActivityRepository::class])
public class FakeTraktActivityRepository : TraktActivityRepository {

    private val fetchInvocations = mutableListOf<Boolean>()
    private var clearAllInvocationCount = 0

    public fun fetchInvocations(): List<Boolean> = fetchInvocations.toList()

    public fun clearAllInvocationCount(): Int = clearAllInvocationCount

    override suspend fun fetchLatestActivities(forceRefresh: Boolean) {
        fetchInvocations.add(forceRefresh)
    }

    override suspend fun clearAllActivities() {
        clearAllInvocationCount++
        fetchInvocations.clear()
    }
}
