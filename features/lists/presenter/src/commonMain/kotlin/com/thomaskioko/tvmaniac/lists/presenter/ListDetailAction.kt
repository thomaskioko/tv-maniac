package com.thomaskioko.tvmaniac.lists.presenter

public sealed interface ListDetailAction {
    public data class ShowClicked(val tmdbId: Long) : ListDetailAction

    public data class RemoveRequested(val tmdbId: Long) : ListDetailAction

    public data object RemoveConfirmed : ListDetailAction

    public data object RemoveDismissed : ListDetailAction

    public data object RetryLoadMore : ListDetailAction

    public data object DismissErrorMessage : ListDetailAction

    public data object BackClicked : ListDetailAction
}
