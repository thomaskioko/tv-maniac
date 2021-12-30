package com.thomaskioko.tvmaniac.di

import com.thomaskioko.tvmaniac.interactor.EpisodesInteractor
import com.thomaskioko.tvmaniac.interactor.GetGenresInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowInteractor
import com.thomaskioko.tvmaniac.interactor.GetShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.interactor.GetTrailersInteractor
import com.thomaskioko.tvmaniac.interactor.GetWatchListInteractor
import com.thomaskioko.tvmaniac.interactor.ObserveShowsByCategoryInteractor
import com.thomaskioko.tvmaniac.interactor.SeasonsInteractor
import com.thomaskioko.tvmaniac.interactor.UpdateWatchlistInteractor

class DataSourceModule(
    private val repositoryModule: RepositoryModule
) {

    val episodesInteractor: EpisodesInteractor by lazy {
        EpisodesInteractor(
            repository = repositoryModule.episodeRepository
        )
    }

    val getGenresInteractor: GetGenresInteractor by lazy {
        GetGenresInteractor(
            repository = repositoryModule.genreRepository
        )
    }

    val getShowInteractor: GetShowInteractor by lazy {
        GetShowInteractor(
            repository = repositoryModule.tvShowsRepository
        )
    }

    val getShowsByCategoryInteractor: GetShowsByCategoryInteractor by lazy {
        GetShowsByCategoryInteractor(
            repository = repositoryModule.tvShowsRepository
        )
    }

    val getTrailersInteractor: GetTrailersInteractor by lazy {
        GetTrailersInteractor(
            repository = repositoryModule.trailerRepository
        )
    }

    val observeShowsByCategoryInteractor: ObserveShowsByCategoryInteractor by lazy {
        ObserveShowsByCategoryInteractor(
            repository = repositoryModule.tvShowsRepository
        )
    }

    val getWatchListInteractor: GetWatchListInteractor by lazy {
        GetWatchListInteractor(
            repository = repositoryModule.tvShowsRepository
        )
    }

    val seasonsInteractor: SeasonsInteractor by lazy {
        SeasonsInteractor(
            repository = repositoryModule.seasonsRepository
        )
    }

    val updateWatchlistInteractor: UpdateWatchlistInteractor by lazy {
        UpdateWatchlistInteractor(
            repository = repositoryModule.tvShowsRepository
        )
    }
}
