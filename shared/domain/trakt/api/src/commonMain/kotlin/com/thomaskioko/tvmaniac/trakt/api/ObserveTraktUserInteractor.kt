package com.thomaskioko.tvmaniac.trakt.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktUiUser.Companion.EmptyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveTraktUserInteractor constructor(
    private val repository: TraktRepository
) : FlowInteractor<String, TraktUiUser>() {

    override fun run(params: String): Flow<TraktUiUser> =
        repository.observeMe(params).map { result ->
            result.data?.let { traktUser ->
                TraktUiUser(
                    slug = traktUser.slug,
                    userName = traktUser.user_name,
                    fullName = traktUser.full_name,
                    profilePicUrl = traktUser.profile_picture
                )
            } ?: EmptyUser
        }
}

data class TraktUiUser(
    val slug: String,
    val userName: String,
    val fullName: String?,
    val profilePicUrl: String?
) {
    companion object {
        val EmptyUser = TraktUiUser(
            slug = "",
            userName = "",
            fullName = null,
            profilePicUrl = null
        )
    }
}