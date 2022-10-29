package com.thomaskioko.tvmaniac.trakt.api.cache

import com.thomaskioko.tvmaniac.core.db.Trakt_list
import kotlinx.coroutines.flow.Flow

interface TraktListCache {

    fun insert(traktList: Trakt_list)

    fun getTraktList(): Trakt_list?

    fun observeTraktList(): Flow<Trakt_list?>
}