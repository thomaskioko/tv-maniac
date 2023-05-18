package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import me.tatarka.inject.annotations.Inject
import kotlin.math.roundToInt

fun TraktUserResponse.toUser(
    slug: String,
) = User(
    slug = ids.slug,
    full_name = name,
    user_name = userName,
    profile_picture = images.avatar.full,
    is_me = slug == "me",
)
