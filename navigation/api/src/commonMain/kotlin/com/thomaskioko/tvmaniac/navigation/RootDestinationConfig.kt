package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootDestinationConfig {
    @Serializable
    data object Home : RootDestinationConfig

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
