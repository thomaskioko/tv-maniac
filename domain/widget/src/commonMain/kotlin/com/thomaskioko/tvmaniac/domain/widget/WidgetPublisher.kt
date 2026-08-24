package com.thomaskioko.tvmaniac.domain.widget

public interface WidgetPublisher {

    public suspend fun hasInstalledWidgets(): Boolean

    public suspend fun publish(shows: List<WidgetShow>)
}
