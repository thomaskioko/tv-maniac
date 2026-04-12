package com.thomaskioko.tvmaniac.presentation.progress

import com.arkivanov.decompose.ComponentContext
import com.thomaskioko.tvmaniac.core.base.ProgressChildScope
import com.thomaskioko.tvmaniac.core.base.TabScope
import com.thomaskioko.tvmaniac.presentation.calendar.CalendarPresenter
import com.thomaskioko.tvmaniac.presentation.upnext.UpNextPresenter
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

@GraphExtension(ProgressChildScope::class)
public interface ProgressChildGraph {
    public val upNextPresenter: UpNextPresenter
    public val calendarPresenter: CalendarPresenter

    @ContributesTo(TabScope::class)
    @GraphExtension.Factory
    public interface Factory {
        public fun createGraph(
            @Provides componentContext: ComponentContext,
        ): ProgressChildGraph
    }
}
