package com.thomaskioko.tvmaniac.profile.api

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(slug: String): Flow<Either<Failure, User>>
    suspend fun clearProfile()
}
