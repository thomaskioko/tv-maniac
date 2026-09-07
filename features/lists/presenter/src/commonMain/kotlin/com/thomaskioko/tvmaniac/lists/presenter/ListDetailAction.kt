package com.thomaskioko.tvmaniac.lists.presenter

public sealed interface ListDetailAction {
    public data class ShowClicked(val tmdbId: Long) : ListDetailAction

    public data class RemoveRequested(val tmdbId: Long) : ListDetailAction

    public data object RemoveConfirmed : ListDetailAction

    public data object RemoveDismissed : ListDetailAction

    public data object RenameRequested : ListDetailAction

    public data class RenameNameChanged(val name: String) : ListDetailAction

    public data object RenameConfirmed : ListDetailAction

    public data object RenameDismissed : ListDetailAction

    public data object DeleteRequested : ListDetailAction

    public data object DeleteConfirmed : ListDetailAction

    public data object DeleteDismissed : ListDetailAction

    public data object RefreshList : ListDetailAction

    public data object RetryLoadMore : ListDetailAction

    public data object DismissErrorMessage : ListDetailAction

    public data object BackClicked : ListDetailAction
}
