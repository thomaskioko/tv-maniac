package com.thomaskioko.tvmaniac.domain.seasondetails.model

import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes

data class SeasonDetailsResult(
  val seasonDetails: SeasonDetailsWithEpisodes,
  val images: List<SeasonImages>,
  val cast: List<SeasonCast>,
)
