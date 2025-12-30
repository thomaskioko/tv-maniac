package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

public interface ShowDetailsDao {
    public fun observeTvShows(id: Long): Flow<TvshowDetails>

    public fun getTvShow(id: Long): TvshowDetails

    public fun deleteTvShow(id: Long)
}
