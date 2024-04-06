package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.presentation.seasondetails.model.SeasonDetailsUiParam
import kotlinx.serialization.Serializable

@Serializable
sealed interface Config {
  @Serializable data object Discover : Config

  @Serializable data object Library : Config

  @Serializable data object Search : Config

  @Serializable data class SeasonDetails(val param: SeasonDetailsUiParam) : Config

  @Serializable data class ShowDetails(val id: Long) : Config

  @Serializable data class MoreShows(val id: Long) : Config

  @Serializable data object Settings : Config

  @Serializable data class Trailers(val id: Long) : Config
}
