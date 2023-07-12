package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.resourcemanager.api.LastRequest
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class ProfileStore(
    private val remoteDataSource: TraktUserRemoteDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val profileDao: ProfileDao,
    private val logger: KermitLogger,
    private val scope: AppCoroutineScope,
) : Store<String, User> by StoreBuilder.from<String, User, User>(
    fetcher = Fetcher.of { slug ->

        when (val apiResult = remoteDataSource.getUser(slug)) {
            is ApiResponse.Success -> apiResult.body.toUser(slug)

            is ApiResponse.Error.GenericError -> {
                logger.error("ProfileStore GenericError", "${apiResult.message}")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error(" ProfileStore HttpError", "${apiResult.code} - ${apiResult.errorBody}")
                throw Throwable("${apiResult.code} - ${apiResult.errorMessage}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("ProfileStore SerializationError", "${apiResult.message}")
                throw Throwable("${apiResult.errorMessage}")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _ -> profileDao.observeUser() },
        writer = { _, user ->
            profileDao.insert(user)

            requestManagerRepository.insert(
                LastRequest(
                    entityId = 0,
                    requestType = "USER_PROFILE",
                ),
            )
        },
        delete = profileDao::delete,
        deleteAll = profileDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
