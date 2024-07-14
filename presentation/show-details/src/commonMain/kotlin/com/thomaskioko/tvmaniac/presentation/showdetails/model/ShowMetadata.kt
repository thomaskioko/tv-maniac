package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.collections.immutable.ImmutableList

data class ShowMetadata(
  val seasons: List<Season>,
  val cast: ImmutableList<Casts>,
  val providers: List<Providers>,
)
