package com.thomaskioko.tvmaniac.profile.api

import com.thomaskioko.tvmaniac.core.db.User
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.StoreReadResponse

interface ProfileRepository {
    fun observeProfile(slug: String): Flow<StoreReadResponse<User>>
    suspend fun clearProfile()
}
