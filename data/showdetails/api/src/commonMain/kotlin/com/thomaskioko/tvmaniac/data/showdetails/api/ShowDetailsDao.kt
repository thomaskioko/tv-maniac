package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

public interface ShowDetailsDao {
    public fun observeTvShowByTraktId(traktId: Long): Flow<TvshowDetails?>

    public fun getTvShow(traktId: Long): TvshowDetails

    public fun getTvShowOrNull(traktId: Long): TvshowDetails?

    public fun deleteTvShow(traktId: Long)
}
