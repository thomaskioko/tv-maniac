package com.thomaskioko.tvmaniac.data.showdetails.api

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import kotlinx.coroutines.flow.Flow

interface ShowDetailsDao {
  fun observeTvShows(id: Long): Flow<TvshowDetails>

  fun getTvShow(id: Long): TvshowDetails

  fun deleteTvShow(id: Long)
}
