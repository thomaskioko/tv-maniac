package com.thomaskioko.tvmaniac.presentation.episodedetail

public sealed interface EpisodeSheetAction {
    public data object ToggleWatched : EpisodeSheetAction
    public data object OpenShow : EpisodeSheetAction
    public data object OpenSeason : EpisodeSheetAction
    public data object Unfollow : EpisodeSheetAction
    public data object Dismiss : EpisodeSheetAction
    public data class MessageShown(val id: Long) : EpisodeSheetAction
    public data object RatingClicked : EpisodeSheetAction
    public data class RatingSelected(val rating: Int) : EpisodeSheetAction
    public data object RatingRemoved : EpisodeSheetAction
    public data object RatingSheetDismissed : EpisodeSheetAction
}
