package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory

public sealed interface SearchShowAction

public data object BackClicked : SearchShowAction
public data object ClearQuery : SearchShowAction
public data class MessageShown(val id: Long) : SearchShowAction
public data object ReloadShowContent : SearchShowAction
public data class QueryChanged(val query: String) : SearchShowAction
public data class SearchShowClicked(val id: Long) : SearchShowAction
public data class CategoryChanged(val category: GenreShowCategory) : SearchShowAction
