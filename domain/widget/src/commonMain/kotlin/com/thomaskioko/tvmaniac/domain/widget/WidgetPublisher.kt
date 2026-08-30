package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow

public interface WidgetPublisher {

    public suspend fun hasInstalledWidgets(): Boolean

    public suspend fun publish(shows: List<WidgetShow>)
}
