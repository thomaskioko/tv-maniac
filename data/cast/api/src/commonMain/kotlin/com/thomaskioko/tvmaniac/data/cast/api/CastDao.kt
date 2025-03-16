package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.db.Cast_appearance
import com.thomaskioko.tvmaniac.db.Casts
import kotlinx.coroutines.flow.Flow

interface CastDao {
  fun upsert(entity: Casts)

  fun upsert(entity: Cast_appearance)

  fun observeShowCast(id: Long): Flow<List<ShowCast>>

  fun observeSeasonCast(id: Long): Flow<List<SeasonCast>>
}
