package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class UpdateFollowingInteractor constructor(
    private val repository: TmdbRepository,
    private val traktRepository: TraktRepository
) : FlowInteractor<UpdateShowParams, Unit>() {

    override fun run(params: UpdateShowParams): Flow<Unit> = flow {

        val user = traktRepository.getLocalTraktUser()
        val favoriteList = traktRepository.getFavoriteList()
        var listId: Long? = null

        repository.updateFollowing(
            showId = params.showId,
            addToWatchList = !params.addToWatchList
        )
        emit(Unit)

        if (user != null) {

            if (favoriteList == null) {
                traktRepository.observeCreateTraktFavoriteList(
                    userSlug = user.slug
                ).collect {
                    listId = it.data?.id
                }
            } else {
                listId = favoriteList.id
            }


            listId?.let { id ->
                if (!params.addToWatchList) {
                    traktRepository.observeAddShowToTraktFavoriteList(
                        userSlug = user.slug,
                        listId = id,
                        tmdbShowId = params.showId
                    ).collect()
                } else {
                    traktRepository.observeRemoveShowFromTraktFavoriteList(
                        userSlug = user.slug,
                        listId = id,
                        tmdbShowId = params.showId
                    ).collect()
                }
            }
        }


    }
}

data class UpdateShowParams(
    val showId: Long,
    val addToWatchList: Boolean
)
