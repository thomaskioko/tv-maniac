package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.serialization.Serializable

@Serializable
data class ShowSeasonDetailsParam(
  val showId: Long,
  val seasonId: Long,
  val seasonNumber: Long,
  val selectedSeasonIndex: Int,
)
