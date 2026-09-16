package com.thomaskioko.tvmaniac.genreshows.presentation

public sealed interface GenreShowsAction

public data class GenreShowClicked(val showId: Long) : GenreShowsAction

public data object GenreShowsBackClicked : GenreShowsAction

public data object RefreshGenreShows : GenreShowsAction

public data object RetryGenreShowsLoadMore : GenreShowsAction

public data object DismissGenreShowsError : GenreShowsAction
