package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.user.api.UserDao
import com.thomaskioko.tvmaniac.data.user.api.UserRemoteDataSource
import com.thomaskioko.tvmaniac.data.user.api.model.RemoteUserProfile
import com.thomaskioko.tvmaniac.db.User
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.USER_PROFILE
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class UserStore(
    private val activeSource: () -> UserRemoteDataSource?,
    private val userDao: UserDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, User> by storeBuilder(
    fetcher = apiFetcher { slug ->
        activeSource()?.getUserProfile(slug)
            ?: ApiResponse.Unauthenticated
    },
    sourceOfTruth = SourceOfTruth.of<String, RemoteUserProfile, User>(
        reader = { key -> userDao.observeUserByKey(key) },
        writer = { username, response ->
            withContext(dispatchers.databaseWrite) {
                userDao.upsertUser(
                    slug = response.slug,
                    userName = response.username,
                    fullName = response.fullName,
                    profilePicture = response.avatarUrl,
                    backgroundUrl = response.backgroundUrl,
                    isMe = username == "me",
                )

                requestManagerRepository.upsert(
                    entityId = USER_PROFILE.requestId,
                    requestType = USER_PROFILE.name,
                )
            }
        },
        deleteAll = { userDao.deleteAll() },
    )
        .usingDispatchers(
            readDispatcher = dispatchers.databaseRead,
            writeDispatcher = dispatchers.databaseWrite,
        ),
).validator(
    Validator.by { _ ->
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = USER_PROFILE.name,
                threshold = USER_PROFILE.duration,
            )
        }
    },
).build()
