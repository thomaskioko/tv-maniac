package com.thomaskioko.tvmaniac.search.presenter

public sealed interface SearchShowAction

public data object ClearQuery : SearchShowAction
public data object DismissSnackBar : SearchShowAction
public data object ReloadShowContent : SearchShowAction
public data object LoadDiscoverShows : SearchShowAction
public data class QueryChanged(val query: String) : SearchShowAction
public data class SearchShowClicked(val id: Long) : SearchShowAction
public data class GenreCategoryClicked(val id: Long) : SearchShowAction
