package com.thomaskioko.tvmaniac.discover.presenter.featured

public sealed interface DiscoverFeaturedAction

public data class FeaturedShowClicked(val showId: Long) : DiscoverFeaturedAction
