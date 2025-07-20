package com.thomaskioko.tvmaniac.search.presenter

import com.arkivanov.decompose.ComponentContext
import com.thomakioko.tvmaniac.util.testing.FakeFormatterUtil
import com.thomaskioko.tvmaniac.genre.FakeGenreRepository
import com.thomaskioko.tvmaniac.search.testing.FakeSearchRepository

class FakeSearchPresenterFactory : SearchShowsPresenter.Factory {

    private val searchRepository = FakeSearchRepository()
    private val genreRepository = FakeGenreRepository()
    private val mapper = Mapper(formatterUtil = FakeFormatterUtil())

    override fun invoke(
        componentContext: ComponentContext,
        onNavigateToShowDetails: (id: Long) -> Unit,
        onNavigateToGenre: (id: Long) -> Unit,
    ): SearchShowsPresenter = SearchShowsPresenter(
        componentContext = componentContext,
        onNavigateToShowDetails = onNavigateToShowDetails,
        onNavigateToGenre = onNavigateToGenre,
        mapper = mapper,
        searchRepository = searchRepository,
        genreRepository = genreRepository,
    )
}
