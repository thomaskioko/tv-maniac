package com.thomaskioko.tvmaniac.genre

import com.thomaskioko.tvmaniac.db.Tvshow
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeGenreRepository : GenreRepository {
  private var entityListResult: Channel<Either<Failure, List<ShowGenresEntity>>> =
    Channel(Channel.UNLIMITED)

  private var showListResult: Channel<Either<Failure, List<Tvshow>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setGenreResult(result: Either<Failure, List<ShowGenresEntity>>) {
    entityListResult.send(result)
  }

  suspend fun setShowResult(result: Either<Failure, List<Tvshow>>) {
    showListResult.send(result)
  }

  override fun observeGenresWithShows(forceRefresh: Boolean): Flow<Either<Failure, List<ShowGenresEntity>>> {
    return entityListResult.receiveAsFlow()
  }

  override suspend fun observeGenreByShowId(id: String, forceRefresh: Boolean): Flow<Either<Failure, List<Tvshow>>> {
    return showListResult.receiveAsFlow()
  }
}
