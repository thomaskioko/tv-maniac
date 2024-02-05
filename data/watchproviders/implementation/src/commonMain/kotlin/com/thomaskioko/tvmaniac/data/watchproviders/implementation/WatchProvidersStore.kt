package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.core.db.WatchProviders
import com.thomaskioko.tvmaniac.core.db.Watch_providers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCH_PROVIDERS
import com.thomaskioko.tvmaniac.tmdb.api.TmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.util.model.ApiResponse
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class WatchProvidersStore(
  private val remoteDataSource: TmdbShowDetailsNetworkDataSource,
  private val dao: WatchProviderDao,
  private val scope: AppCoroutineScope,
  private val formatterUtil: FormatterUtil,
  private val requestManagerRepository: RequestManagerRepository,
) :
  Store<Long, List<WatchProviders>> by StoreBuilder.from(
      fetcher =
        Fetcher.of { id ->
          when (val response = remoteDataSource.getShowWatchProviders(id)) {
            is ApiResponse.Success -> response.body
            is ApiResponse.Error.GenericError -> throw Throwable("${response.errorMessage}")
            is ApiResponse.Error.HttpError ->
              throw Throwable("${response.code} - ${response.errorMessage}")
            is ApiResponse.Error.SerializationError -> throw Throwable("${response.errorMessage}")
          }
        },
      sourceOfTruth =
        SourceOfTruth.of(
          reader = { id -> dao.observeWatchProviders(id) },
          writer = { id, response ->

            // TODO:: Get users locale and format the date accordingly.
            response.results.US?.let { usProvider ->
              usProvider.free.forEach {
                dao.upsert(
                  Watch_providers(
                    id = Id(it.providerId.toLong()),
                    logo_path =
                      it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                    name = it.providerName,
                    tmdb_id = Id(id),
                  ),
                )
              }
              usProvider.flatrate.forEach {
                dao.upsert(
                  Watch_providers(
                    id = Id(it.providerId.toLong()),
                    logo_path =
                      it.logoPath?.let { path -> formatterUtil.formatTmdbPosterPath(path) },
                    name = it.providerName,
                    tmdb_id = Id(id),
                  ),
                )
              }
            }

            // Update Last Request
            requestManagerRepository.insert(
              entityId = id,
              requestType = WATCH_PROVIDERS.name,
            )
          },
          delete = dao::delete,
          deleteAll = dao::deleteAll,
        ),
    )
    .scope(scope.io)
    .build()
