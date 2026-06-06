package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

public interface ShowDetailsDao {
    public fun observeTvShowByTraktId(showId: Long): Flow<TvshowDetails?>

    public fun getTvShow(showId: Long): TvshowDetails

    public fun getTvShowOrNull(showId: Long): TvshowDetails?

    public fun deleteTvShow(showId: Long)
}
