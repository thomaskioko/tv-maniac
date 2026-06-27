package com.thomaskioko.tvmaniac.showdetails.ui

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsState
import com.thomaskioko.tvmaniac.presenter.showdetails.cast.ShowDetailsCastState
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderState
import com.thomaskioko.tvmaniac.presenter.showdetails.model.CastModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ContinueTrackingEpisodeModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ProviderModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.ShowModel
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.presenter.showdetails.providers.ShowDetailsProvidersState
import com.thomaskioko.tvmaniac.presenter.showdetails.seasonsepisodes.ShowDetailsSeasonsEpisodesState
import com.thomaskioko.tvmaniac.presenter.showdetails.similar.ShowDetailsSimilarState
import com.thomaskioko.tvmaniac.presenter.showdetails.trailers.ShowDetailsTrailersState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

internal val previewHeaderState = ShowDetailsHeaderState(
    tmdbId = 84958,
    title = "Loki",
    overview = "After stealing the Tesseract during the events of \"Avengers: Endgame,\" an alternate " +
        "version of Loki is brought to the mysterious Time Variance Authority, a bureaucratic organization " +
        "that exists outside of time and space and monitors the timeline.",
    language = "en",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    backdropImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    year = "2024",
    status = "Returning Series",
    votes = 4958,
    rating = 8.1,
    isInLibrary = true,
    genres = persistentListOf("Horror", "Action"),
    canAddToList = true,
)

internal val previewHeaderStateSimkl = previewHeaderState.copy(canAddToList = false)

internal val previewSeasonsEpisodesState = ShowDetailsSeasonsEpisodesState(
    seasonsList = persistentListOf(
        SeasonModel(
            seasonId = 114355L,
            tvShowId = 84958L,
            name = "Season 1",
            seasonNumber = 1L,
        ),
    ),
    numberOfSeasons = 1,
    watchedEpisodesCount = 0,
    totalEpisodesCount = 6,
    watchProgress = 0f,
    selectedSeasonIndex = -1,
    continueTrackingEpisodes = persistentListOf(
        ContinueTrackingEpisodeModel(
            episodeId = 121L,
            seasonId = 1L,
            showId = 1L,
            episodeNumber = 1,
            seasonNumber = 2,
            episodeNumberFormatted = "S01 | E01",
            episodeTitle = "Season Premiere",
            imageUrl = null,
            isWatched = false,
            daysUntilAir = null,
            hasAired = true,
        ),
        ContinueTrackingEpisodeModel(
            episodeId = 122L,
            seasonId = 1L,
            showId = 1L,
            episodeNumber = 2,
            seasonNumber = 2,
            episodeNumberFormatted = "S01 | E02",
            episodeTitle = "The Aftermath",
            imageUrl = null,
            isWatched = false,
            daysUntilAir = null,
            hasAired = true,
        ),
    ),
    continueTrackingScrollIndex = 0,
    updatingEpisodeIds = persistentSetOf(),
)

internal val previewCastState = ShowDetailsCastState(
    castsList = persistentListOf(
        CastModel(
            id = 1L,
            name = "Character",
            profileUrl = null,
            characterName = "Starring",
        ),
    ),
)

internal val previewProvidersState = ShowDetailsProvidersState(
    providers = persistentListOf(
        ProviderModel(
            id = 1L,
            logoUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            name = "Netflix",
        ),
    ),
)

internal val previewTrailersState = ShowDetailsTrailersState(
    trailersList = persistentListOf(
        TrailerModel(
            showId = 1232L,
            key = "1",
            name = "Official Trailer",
            youtubeThumbnailUrl = "",
        ),
    ),
    hasWebViewInstalled = false,
)

internal val previewSimilarState = ShowDetailsSimilarState(
    similarShows = persistentListOf(
        ShowModel(
            showId = 1232L,
            title = "Loki",
            posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
            backdropImageUrl = null,
            isInLibrary = false,
        ),
    ),
)

internal val previewHostState = ShowDetailsState()

internal val previewHostStateWithMessage = ShowDetailsState(
    message = UiMessage(message = "Oops! Something went wrong"),
)
