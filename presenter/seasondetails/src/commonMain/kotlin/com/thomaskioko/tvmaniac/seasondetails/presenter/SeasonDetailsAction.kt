package com.thomaskioko.tvmaniac.seasondetails.presenter

sealed interface SeasonDetailsAction

data object SeasonDetailsBackClicked : SeasonDetailsAction

data object OnEpisodeHeaderClicked : SeasonDetailsAction

data object ShowGallery : SeasonDetailsAction

data class MarkSeasonAsWatched(
    val hasUnwatchedInPreviousSeasons: Boolean,
) : SeasonDetailsAction

data object MarkSeasonAsUnwatched : SeasonDetailsAction

data object DismissDialog : SeasonDetailsAction

data object ConfirmDialogAction : SeasonDetailsAction

data object SecondaryDialogAction : SeasonDetailsAction

data object ReloadSeasonDetails : SeasonDetailsAction

data class MarkEpisodeWatched(
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val hasPreviousUnwatched: Boolean,
) : SeasonDetailsAction

data class MarkEpisodeUnwatched(
    val episodeId: Long,
) : SeasonDetailsAction

data class ToggleEpisodeWatched(
    val episodeId: Long,
) : SeasonDetailsAction

data object ToggleSeasonWatched : SeasonDetailsAction

data class EpisodeClicked(
    val id: Long,
) : SeasonDetailsAction
