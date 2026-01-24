package com.thomaskioko.tvmaniac.data.user.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.data.user.api.UserDao
import com.thomaskioko.tvmaniac.db.User
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.USER_PROFILE
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

@Inject
public class UserStore(
    private val traktUserRemoteDataSource: TraktUserRemoteDataSource,
    private val userDao: UserDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<String, User> by storeBuilder(
    fetcher = apiFetcher { slug ->
        traktUserRemoteDataSource.getUser(slug)
    },
    sourceOfTruth = SourceOfTruth.of<String, TraktUserResponse, User>(
        reader = { key -> userDao.observeUserByKey(key) },
        writer = { username, response ->
            withContext(dispatchers.databaseWrite) {
                userDao.upsertUser(
                    slug = response.ids.slug,
                    userName = response.userName,
                    fullName = response.name,
                    profilePicture = response.images.avatar.full,
                    backgroundUrl = null,
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
    Validator.by { user ->
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = USER_PROFILE.name,
                threshold = USER_PROFILE.duration,
            )
        }
    },
).build()
