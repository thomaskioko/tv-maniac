package com.thomaskioko.tvmaniac.seasondetails.api

import com.thomaskioko.tvmaniac.db.SeasonImages
import com.thomaskioko.tvmaniac.seasondetails.api.model.ContinueTrackingResult
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.flow.Flow

public interface SeasonDetailsRepository {
    public suspend fun fetchSeasonDetails(
        param: SeasonDetailsParam,
        forceRefresh: Boolean = false,
    )

    public suspend fun syncShowSeasonDetails(
        showId: Long,
        forceRefresh: Boolean = false,
        refreshLatestSeason: Boolean = false,
    )

    /** Whether the per-show season-details sync gate has expired for [showId]. */
    public suspend fun isShowSeasonSyncExpired(showId: Long): Boolean

    public suspend fun syncPreviousSeasonsEpisodes(
        showId: Long,
        beforeSeasonNumber: Long,
        forceRefresh: Boolean = false,
    )

    public fun observeSeasonDetails(
        param: SeasonDetailsParam,
    ): Flow<SeasonDetailsWithEpisodes>

    public fun observeSeasonImages(id: Long): Flow<List<SeasonImages>>

    public fun observeContinueTrackingEpisodes(showId: Long): Flow<ContinueTrackingResult?>
}
