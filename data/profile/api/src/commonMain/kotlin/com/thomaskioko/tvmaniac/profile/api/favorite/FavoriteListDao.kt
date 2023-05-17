package com.thomaskioko.tvmaniac.profile.api.favorite

import com.thomaskioko.tvmaniac.core.db.Trakt_shows_list
import kotlinx.coroutines.flow.Flow

interface FavoriteListDao {

    fun insert(traktList: Trakt_shows_list)

    fun getTraktList(): Trakt_shows_list?

    fun observeTraktList(): Flow<Trakt_shows_list>
}
