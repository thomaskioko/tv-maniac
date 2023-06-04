package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.core.networkutil.NetworkRepository
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse

@OptIn(ExperimentalStoreApi::class)
@Inject
class ProfileRepositoryImpl constructor(
    private val store: ProfileStore,
    private val networkRepository: NetworkRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ProfileRepository {

    override fun observeProfile(slug: String): Flow<StoreReadResponse<User>> =
        store.stream(StoreReadRequest.cached(key = slug, refresh = networkRepository.isConnected()))
            .flowOn(dispatchers.io)

    override suspend fun clearProfile() {
        store.clear()
    }
}
