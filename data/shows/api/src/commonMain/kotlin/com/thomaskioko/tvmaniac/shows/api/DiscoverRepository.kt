package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.core.db.ShowById
import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

interface DiscoverRepository {

    fun observeShow(traktId: Long): Flow<Either<Failure, ShowById>>

    suspend fun getShowById(traktId: Long): ShowById

}
