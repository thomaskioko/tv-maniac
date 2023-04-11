package com.thomaskioko.tvmaniac.trakt.profile.implementation

import com.thomaskioko.tvmaniac.base.util.DateFormatter
import com.thomaskioko.tvmaniac.base.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import kotlin.math.roundToInt

//TODO:: Format to a class and inject it.

fun TraktUserStatsResponse.toCache(
    slug: String,
    formatterUtil: FormatterUtil
) = TraktStats(
    user_slug = slug,
    collected_shows = shows.collected.toString(),
    months = (episodes.minutes / 43800).toDouble().roundToInt().toString(),
    days = (episodes.minutes / 1440).toDouble().roundToInt().toString(),
    hours = formatterUtil.formatDuration(episodes.minutes / 60),
    episodes_watched = formatterUtil.formatDuration(episodes.watched)
)

fun TraktCreateListResponse.toCache() = Trakt_list(
    id = ids.trakt.toLong(),
    slug = ids.slug,
    description = description
)

fun TraktUserResponse.toCache(slug: String) = Trakt_user(
    slug = ids.slug,
    full_name = name,
    user_name = userName,
    profile_picture = images.avatar.full,
    is_me = slug == "me"
)

fun List<TraktFollowedShowResponse>.responseToCache(
    dateFormatter: DateFormatter
) = map {
    Followed_shows(
        id = it.show.ids.trakt.toLong(),
        synced = true,
        created_at = dateFormatter.getTimestampMilliseconds()
    )
}
