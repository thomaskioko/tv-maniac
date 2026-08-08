package com.thomaskioko.tvmaniac.statistics.presenter

public sealed interface StatisticsAction {
    public data object BackClicked : StatisticsAction

    public data object UpgradeClicked : StatisticsAction

    public data object Refresh : StatisticsAction

    public data class ShowClicked(val showId: Long) : StatisticsAction

    public data class MessageShown(val id: Long) : StatisticsAction
}
