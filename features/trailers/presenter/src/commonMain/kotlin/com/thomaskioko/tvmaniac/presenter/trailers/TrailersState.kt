package com.thomaskioko.tvmaniac.presenter.trailers

import com.thomaskioko.tvmaniac.presenter.trailers.model.Trailer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public interface TrailersState {
    public val title: String
}

public data class LoadingTrailers(
    override val title: String,
) : TrailersState

public data class TrailersContent(
    override val title: String,
    val moreTrailersTitle: String,
    val selectedVideoKey: String? = null,
    val trailersList: ImmutableList<Trailer> = persistentListOf(),
) : TrailersState

public data class TrailerError(
    override val title: String,
    val errorMessage: String,
    val retryLabel: String,
) : TrailersState
