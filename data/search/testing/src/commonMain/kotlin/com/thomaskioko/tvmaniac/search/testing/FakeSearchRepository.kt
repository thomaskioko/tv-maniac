package com.thomaskioko.tvmaniac.search.testing

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.search.api.SearchRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSearchRepository : SearchRepository {
  private var eitherChannel: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSearchResult(result: Either<Failure, List<ShowEntity>>) {
    eitherChannel.send(result)
  }

  override suspend fun search(query: String): Flow<Either<Failure, List<ShowEntity>>>
   = eitherChannel.receiveAsFlow()
}
