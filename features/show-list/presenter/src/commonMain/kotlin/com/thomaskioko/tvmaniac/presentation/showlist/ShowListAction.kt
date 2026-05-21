package com.thomaskioko.tvmaniac.presentation.showlist

public sealed interface ShowListAction {
    public data object Login : ShowListAction
    public data object ShowCreateListField : ShowListAction
    public data object DismissCreateListField : ShowListAction
    public data class UpdateCreateListName(val name: String) : ShowListAction
    public data object CreateListSubmitted : ShowListAction
    public data class ToggleShowInList(
        val listId: Long,
        val isCurrentlyInList: Boolean,
    ) : ShowListAction
    public data object Dismiss : ShowListAction
    public data class MessageShown(val id: Long) : ShowListAction
}
