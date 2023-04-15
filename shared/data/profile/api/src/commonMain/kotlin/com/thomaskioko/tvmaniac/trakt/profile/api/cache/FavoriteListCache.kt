package com.thomaskioko.tvmaniac.trakt.profile.api.cache

import com.thomaskioko.tvmaniac.core.db.Trakt_list
import kotlinx.coroutines.flow.Flow

interface FavoriteListCache {

    fun insert(traktList: Trakt_list)

    fun getTraktList(): Trakt_list?

    fun observeTraktList(): Flow<Trakt_list>
}