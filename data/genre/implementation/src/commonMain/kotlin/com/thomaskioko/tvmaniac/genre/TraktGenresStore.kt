package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.genre.model.TraktGenreEntity
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.TRAKT_GENRES
import com.thomaskioko.tvmaniac.trakt.api.TraktShowsRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktGenreResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class TraktGenresStore(
    private val traktRemoteDataSource: TraktShowsRemoteDataSource,
    private val traktGenreDao: TraktGenreDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<TraktGenreEntity>> by storeBuilder(
    fetcher = Fetcher.of {
        traktRemoteDataSource.getGenres().getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of<Unit, List<TraktGenreResponse>, List<TraktGenreEntity>>(
        reader = { traktGenreDao.observeGenres() },
        writer = { _, response ->
            withContext(dispatchers.databaseWrite) {
                requestManagerRepository.upsert(
                    entityId = TRAKT_GENRES.requestId,
                    requestType = TRAKT_GENRES.name,
                )
                response.forEach { genre ->
                    traktGenreDao.upsertGenre(slug = genre.slug, name = genre.name)
                }
            }
        },
        delete = { traktGenreDao.deleteAllGenres() },
        deleteAll = traktGenreDao::deleteAllGenres,
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = TRAKT_GENRES.name,
                threshold = TRAKT_GENRES.duration,
            )
        }
    },
).build()
