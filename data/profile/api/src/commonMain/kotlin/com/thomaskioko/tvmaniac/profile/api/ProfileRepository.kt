package com.thomaskioko.tvmaniac.profile.api

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(slug: String): Flow<Either<Failure, User>>
    suspend fun clearProfile()
}
