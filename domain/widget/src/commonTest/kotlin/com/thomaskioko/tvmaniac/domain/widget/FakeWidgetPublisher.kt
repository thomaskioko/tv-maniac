package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow

class FakeWidgetPublisher : WidgetPublisher {
    private var installed: Boolean = false
    private var failure: Exception? = null
    private var publishedShows: List<WidgetShow>? = null

    fun setInstalled(installed: Boolean) {
        this.installed = installed
    }

    fun setFailure(failure: Exception?) {
        this.failure = failure
    }

    fun getPublishedShows(): List<WidgetShow>? = publishedShows

    override suspend fun hasInstalledWidgets(): Boolean = installed

    override suspend fun publish(shows: List<WidgetShow>) {
        failure?.let { throw it }
        publishedShows = shows
    }
}
