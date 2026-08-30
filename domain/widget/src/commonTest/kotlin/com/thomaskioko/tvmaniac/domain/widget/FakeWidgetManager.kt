package com.thomaskioko.tvmaniac.domain.widget

class FakeWidgetManager : WidgetManager {
    private var installed: Boolean = false
    private var path: String? = null
    private var reloadCount: Int = 0

    fun setInstalled(installed: Boolean) {
        this.installed = installed
    }

    fun setContainerPath(path: String?) {
        this.path = path
    }

    fun getReloadCount(): Int = reloadCount

    override fun hasInstalledWidgets(onResult: (Boolean) -> Unit) {
        onResult(installed)
    }

    override fun containerPath(): String? = path

    override fun reloadTimelines() {
        reloadCount++
    }
}
