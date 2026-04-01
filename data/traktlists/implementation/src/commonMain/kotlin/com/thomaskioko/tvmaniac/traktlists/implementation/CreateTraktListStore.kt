package com.thomaskioko.tvmaniac.traktlists.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.traktlists.api.TraktListDao
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store

public data class CreateTraktListParams(
    val slug: String,
    val name: String,
)

@Inject
public class CreateTraktListStore(
    private val traktListRemoteDataSource: TraktListRemoteDataSource,
    private val traktListDao: TraktListDao,
    private val dateTimeProvider: DateTimeProvider,
) : Store<CreateTraktListParams, Unit> by storeBuilder(
    fetcher = apiFetcher { params: CreateTraktListParams ->
        traktListRemoteDataSource.createList(userSlug = params.slug, name = params.name)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: CreateTraktListParams -> flowOf(Unit) },
        writer = { _, response ->
            traktListDao.upsert(
                TraktListEntity(
                    id = response.ids.trakt.toLong(),
                    slug = response.ids.slug,
                    name = response.name,
                    description = response.description,
                    itemCount = 0,
                    createdAt = dateTimeProvider.now().toString(),
                ),
            )
        },
    ),
).build()
