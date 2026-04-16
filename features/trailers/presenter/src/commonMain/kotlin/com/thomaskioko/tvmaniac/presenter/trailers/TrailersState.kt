package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public interface TrailersState

public data object LoadingTrailers : TrailersState

public data class TrailersContent(
    val selectedVideoKey: String? = null,
    val trailersList: ImmutableList<Trailer> = persistentListOf(),
) : TrailersState

public data class TrailerError(val errorMessage: String?) : TrailersState
