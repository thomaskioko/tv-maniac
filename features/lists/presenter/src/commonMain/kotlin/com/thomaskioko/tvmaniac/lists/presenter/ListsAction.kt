package com.thomaskioko.tvmaniac.lists.presenter

public sealed interface ListsAction {
    public data class ListClicked(val listId: Long) : ListsAction

    public data object BackClicked : ListsAction
}
