package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootDestinationConfig {
    @Serializable
    data object Home : RootDestinationConfig

    @Serializable
    data object Profile : RootDestinationConfig

    @Serializable
    data object Settings : RootDestinationConfig

    @Serializable
    data class ShowDetails(val id: Long) : RootDestinationConfig

    @Serializable
    data class SeasonDetails(val param: SeasonDetailsUiParam) : RootDestinationConfig

    @Serializable
    data class MoreShows(val id: Long) : RootDestinationConfig

    @Serializable
    data class Trailers(val id: Long) : RootDestinationConfig

    @Serializable
    data class GenreShows(val id: Long) : RootDestinationConfig
}
