package com.thomaskioko.tvmaniac.profile.implementation

import com.thomaskioko.tvmaniac.core.db.User
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse

fun TraktUserResponse.toUser(
    slug: String,
) = User(
    slug = ids.slug,
    full_name = name,
    user_name = userName,
    profile_picture = images.avatar.full,
    is_me = slug == "me",
)
