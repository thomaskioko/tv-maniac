package com.thomaskioko.tvmaniac.discover.presenter.upnext

import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class DiscoverUpNextState(
    val nextEpisodes: ImmutableList<NextEpisodeUiModel> = persistentListOf(),
)
