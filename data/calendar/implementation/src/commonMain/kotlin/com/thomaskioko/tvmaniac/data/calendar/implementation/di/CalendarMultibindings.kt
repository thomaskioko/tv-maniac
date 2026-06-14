package com.thomaskioko.tvmaniac.data.calendar.implementation.di

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
public interface CalendarMultibindings {

    @Multibinds(allowEmpty = true)
    public fun calendarRemoteDataSources(): Set<CalendarRemoteDataSource>
}

@BindingContainer
@ContributesTo(AppScope::class)
public object ActiveCalendarRemoteDataSourceBindingContainer {

    @Provides
    public fun activeCalendarRemoteDataSource(
        sources: Set<CalendarRemoteDataSource>,
        accountManager: AccountManager,
    ): CalendarRemoteDataSource? = sources.firstOrNull { it.provider == accountManager.getActiveProvider() }
}
