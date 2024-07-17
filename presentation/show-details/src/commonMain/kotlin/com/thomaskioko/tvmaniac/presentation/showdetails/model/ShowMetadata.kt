package com.thomaskioko.tvmaniac.presentation.showdetails.model

import kotlinx.collections.immutable.ImmutableList

data class ShowMetadata(
  val seasons: ImmutableList<Season>,
  val cast: ImmutableList<Casts>,
  val providers: ImmutableList<Providers>,
)
