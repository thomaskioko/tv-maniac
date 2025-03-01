package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import kotlinx.coroutines.flow.Flow

interface CastDao {
  fun upsert(entity: Casts)

  fun fetchShowCast(id: Long): List<ShowCast>

  fun observeShowCast(id: Long): Flow<List<ShowCast>>

  fun fetchSeasonCast(id: Long): List<SeasonCast>

  fun observeSeasonCast(id: Long): Flow<List<SeasonCast>>
}
