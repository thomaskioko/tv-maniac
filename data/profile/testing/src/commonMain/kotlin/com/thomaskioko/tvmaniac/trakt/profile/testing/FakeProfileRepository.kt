package com.thomaskioko.tvmaniac.trakt.profile.testing

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreReadResponseOrigin

class FakeProfileRepository : ProfileRepository {

    private val user = User(
        slug = "me",
        user_name = "silly_eyes",
        full_name = "Stranger Danger",
        profile_picture = "",
        is_me = true,
    )

    override fun observeProfile(slug: String): Flow<StoreReadResponse<User>> =
        flowOf(
            StoreReadResponse.Data(
                value = user,
                origin = StoreReadResponseOrigin.Cache,
            ),
        )

    override suspend fun clearProfile() {
        // no-op
    }
}
