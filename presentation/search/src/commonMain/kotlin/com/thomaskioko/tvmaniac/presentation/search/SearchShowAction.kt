package com.thomaskioko.tvmaniac.presentation.search

sealed interface SearchShowAction

data object ClearQuery : SearchShowAction
data object LoadDiscoverShows : SearchShowAction
data class QueryChanged(val query: String) : SearchShowAction
data class SearchShowClicked(val id: Long): SearchShowAction
