package com.thomaskioko.tvmaniac.seasondetails.presenter

public sealed interface SeasonDetailsAction

public data object SeasonDetailsBackClicked : SeasonDetailsAction

public data object OnEpisodeHeaderClicked : SeasonDetailsAction

public data object ShowGallery : SeasonDetailsAction

public data class MarkSeasonAsWatched(
    val hasUnwatchedInPreviousSeasons: Boolean,
) : SeasonDetailsAction

public data object MarkSeasonAsUnwatched : SeasonDetailsAction

public data object DismissDialog : SeasonDetailsAction

public data object ConfirmDialogAction : SeasonDetailsAction

public data object SecondaryDialogAction : SeasonDetailsAction

public data object ReloadSeasonDetails : SeasonDetailsAction

public data class SeasonDetailsMessageShown(val id: Long) : SeasonDetailsAction

public data class MarkEpisodeWatched(
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
    val hasPreviousUnwatched: Boolean,
) : SeasonDetailsAction

public data class MarkEpisodeUnwatched(
    val episodeId: Long,
) : SeasonDetailsAction

public data class ToggleEpisodeWatched(
    val episodeId: Long,
) : SeasonDetailsAction

public data object ToggleSeasonWatched : SeasonDetailsAction

public data class EpisodeClicked(
    val id: Long,
) : SeasonDetailsAction
