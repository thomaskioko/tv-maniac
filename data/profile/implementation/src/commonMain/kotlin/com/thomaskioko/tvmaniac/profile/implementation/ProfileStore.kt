package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.core.networkutil.ApiResponse
import com.thomaskioko.tvmaniac.profile.api.ProfileDao
import com.thomaskioko.tvmaniac.trakt.api.TraktRemoteDataSource
import com.thomaskioko.tvmaniac.util.KermitLogger
import com.thomaskioko.tvmaniac.util.model.AppCoroutineScope
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder

@Inject
class ProfileStore(
    private val traktRemoteDataSource: TraktRemoteDataSource,
    private val profileDao: ProfileDao,
    private val logger: KermitLogger,
    private val scope: AppCoroutineScope,
) : Store<String, User> by StoreBuilder.from<String, User, User, User>(
    fetcher = Fetcher.of { slug ->

        when (val apiResult = traktRemoteDataSource.getUser(slug)) {
            is ApiResponse.Success -> apiResult.body.toUser(slug)

            is ApiResponse.Error.GenericError -> {
                logger.error("GenericError", "${apiResult.errorMessage}")
                throw Throwable("${apiResult.errorMessage}")
            }

            is ApiResponse.Error.HttpError -> {
                logger.error("HttpError", "${apiResult.code} - ${apiResult.errorBody?.message}")
                throw Throwable("${apiResult.code} - ${apiResult.errorBody?.message}")
            }

            is ApiResponse.Error.SerializationError -> {
                logger.error("SerializationError", "$apiResult")
                throw Throwable("$apiResult")
            }
        }
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { slug -> profileDao.observeUserBySlug(slug) },
        writer = { _, user -> profileDao.insert(user) },
        delete = profileDao::delete,
        deleteAll = profileDao::deleteAll,
    ),
)
    .scope(scope.io)
    .build()
