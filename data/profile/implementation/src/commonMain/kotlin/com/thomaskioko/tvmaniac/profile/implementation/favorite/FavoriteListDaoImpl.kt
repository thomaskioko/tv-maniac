package com.thomaskioko.tvmaniac.profile.implementation.favorite

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import com.thomaskioko.tvmaniac.core.db.TvManiacDatabase
import com.thomaskioko.tvmaniac.profile.api.favorite.FavoriteListDao
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FavoriteListDaoImpl(
    private val database: TvManiacDatabase,
    private val dispatchers: AppCoroutineDispatchers,
) : FavoriteListDao {

    override fun insert(traktList: Trakt_shows_list) {
        database.trakt_shows_listQueries.insertOrReplace(
            id = traktList.id,
            slug = traktList.slug,
            description = traktList.description,
        )
    }

    override fun getTraktList(): Trakt_shows_list? =
        database.trakt_shows_listQueries.selectShowsList().executeAsOneOrNull()

    override fun observeTraktList(): Flow<Trakt_shows_list> {
        return database.trakt_shows_listQueries.selectShowsList()
            .asFlow()
            .mapToOne(dispatchers.io)
    }
}
