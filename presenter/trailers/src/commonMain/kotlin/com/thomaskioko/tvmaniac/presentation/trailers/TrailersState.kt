package com.thomaskioko.tvmaniac.presentation.trailers

import com.thomaskioko.tvmaniac.presentation.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface TrailersState

data object LoadingTrailers : TrailersState

data class TrailersContent(
  val selectedVideoKey: String? = null,
  val trailersList: ImmutableList<Trailer> = persistentListOf(),
) : TrailersState

data class TrailerError(val errorMessage: String?) : TrailersState
