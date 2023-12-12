package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.profile.api.ProfileRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.util.extensions.mapResult
import com.thomaskioko.tvmaniac.util.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.StoreReadRequest
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalStoreApi::class, ExperimentalCoroutinesApi::class)
@Inject
class ProfileRepositoryImpl(
    private val store: ProfileStore,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : ProfileRepository {

    override fun observeProfile(slug: String): Flow<Either<Failure, User>> =
        store.stream(
            StoreReadRequest.cached(
                key = slug,
                refresh = requestManagerRepository.isRequestExpired(
                    entityId = 0,
                    requestType = "USER_PROFILE",
                    threshold = 30.days,
                ),
            ),
        )
            .mapResult()
            .flowOn(dispatchers.io)

    override suspend fun clearProfile() {
        store.clear()
    }
}
