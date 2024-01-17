package com.thomaskioko.tvmaniac.data.showdetails.testing

import com.thomaskioko.tvmaniac.core.db.TvshowDetails
import com.thomaskioko.tvmaniac.data.showdetails.api.ShowDetailsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

class FakeShowDetailsRepository: ShowDetailsRepository {
    override suspend fun getShowDetails(id: Long): TvshowDetails {
        TODO("Not yet implemented")
    }

    override fun observeShowDetails(id: Long): Flow<Either<Failure, TvshowDetails>> {
        TODO("Not yet implemented")
    }
}