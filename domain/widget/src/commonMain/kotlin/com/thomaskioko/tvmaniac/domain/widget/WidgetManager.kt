package com.thomaskioko.tvmaniac.domain.widget

public interface WidgetManager {

    public fun hasInstalledWidgets(onResult: (Boolean) -> Unit)

    public fun containerPath(): String?

    public fun reloadTimelines()
}
