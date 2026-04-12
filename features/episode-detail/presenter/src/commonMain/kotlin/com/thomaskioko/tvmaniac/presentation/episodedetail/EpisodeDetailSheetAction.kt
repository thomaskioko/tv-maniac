package com.thomaskioko.tvmaniac.presentation.episodedetail

public sealed interface EpisodeDetailSheetAction {
    public data object ToggleWatched : EpisodeDetailSheetAction
    public data object OpenShow : EpisodeDetailSheetAction
    public data object OpenSeason : EpisodeDetailSheetAction
    public data object Unfollow : EpisodeDetailSheetAction
    public data object Dismiss : EpisodeDetailSheetAction
    public data class MessageShown(val id: Long) : EpisodeDetailSheetAction
}
