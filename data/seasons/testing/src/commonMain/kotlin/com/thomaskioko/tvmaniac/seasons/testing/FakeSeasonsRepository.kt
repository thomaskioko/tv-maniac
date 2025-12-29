package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsResult = MutableStateFlow<List<ShowSeasons>>(emptyList())

    public suspend fun setSeasonsResult(result: List<ShowSeasons>) {
        seasonsResult.emit(result)
    }

    override fun observeSeasonsByShowId(id: Long, includeSpecials: Boolean): Flow<List<ShowSeasons>> = seasonsResult.asStateFlow()
}
