package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.serialization.Serializable

@Serializable
public sealed interface RootDestinationConfig {
    @Serializable
    public data object Home : RootDestinationConfig

    @Serializable
    public data object Profile : RootDestinationConfig

    @Serializable
    public data object Settings : RootDestinationConfig

    @Serializable
    public data class ShowDetails(val param: ShowDetailsParam) : RootDestinationConfig

    @Serializable
    public data class SeasonDetails(val param: SeasonDetailsUiParam) : RootDestinationConfig

    @Serializable
    public data class MoreShows(val id: Long) : RootDestinationConfig

    @Serializable
    public data class Trailers(val id: Long) : RootDestinationConfig

    @Serializable
    public data class GenreShows(val id: Long) : RootDestinationConfig
}
