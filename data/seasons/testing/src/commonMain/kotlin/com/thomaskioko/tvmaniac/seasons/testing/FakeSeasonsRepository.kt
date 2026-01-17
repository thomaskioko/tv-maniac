package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeSeasonsRepository : SeasonsRepository {

    private var seasonsResult = MutableStateFlow<List<ShowSeasons>>(emptyList())

    public fun setSeasonsResult(result: List<ShowSeasons>) {
        seasonsResult.value = result
    }

    override fun observeSeasonsByShowId(id: Long): Flow<List<ShowSeasons>> = seasonsResult.asStateFlow()

    override fun getSeasonsByShowId(id: Long, includeSpecials: Boolean): List<ShowSeasons> =
        seasonsResult.value
}
