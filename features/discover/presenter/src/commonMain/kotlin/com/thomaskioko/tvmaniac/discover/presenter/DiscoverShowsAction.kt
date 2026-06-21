package com.thomaskioko.tvmaniac.discover.presenter

public sealed interface DiscoverShowAction

public data object SearchIconClicked : DiscoverShowAction

public data object RefreshData : DiscoverShowAction

public data class MessageShown(val id: Long) : DiscoverShowAction
