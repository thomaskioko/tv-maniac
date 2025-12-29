package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.db.Cast_appearance
import com.thomaskioko.tvmaniac.db.Casts
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import kotlinx.coroutines.flow.Flow

public interface CastDao {
    public fun upsert(entity: Casts)

    public fun upsert(entity: Cast_appearance)

    public fun observeShowCast(id: Long): Flow<List<ShowCast>>

    public fun observeSeasonCast(id: Long): Flow<List<SeasonCast>>
}
