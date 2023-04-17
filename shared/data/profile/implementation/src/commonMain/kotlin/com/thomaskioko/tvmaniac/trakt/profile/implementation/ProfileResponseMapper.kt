package com.thomaskioko.tvmaniac.trakt.profile.implementation

import com.thomaskioko.tvmaniac.util.DateFormatter
import com.thomaskioko.tvmaniac.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.db.Followed_shows
import com.thomaskioko.tvmaniac.core.db.TraktStats
import com.thomaskioko.tvmaniac.core.db.Trakt_list
import com.thomaskioko.tvmaniac.core.db.Trakt_user
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCreateListResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktFollowedShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import me.tatarka.inject.annotations.Inject
import kotlin.math.roundToInt

@Inject
class ProfileResponseMapper(
    private val formatterUtil: FormatterUtil,
    private val dateFormatter: DateFormatter,
) {

    fun toTraktStats(
        slug: String,
        response: TraktUserStatsResponse,
    ) = TraktStats(
        user_slug = slug,
        collected_shows = response.shows.collected.toString(),
        months = (response.episodes.minutes / 43800).toDouble().roundToInt().toString(),
        days = (response.episodes.minutes / 1440).toDouble().roundToInt().toString(),
        hours = formatterUtil.formatDuration(response.episodes.minutes / 60),
        episodes_watched = formatterUtil.formatDuration(response.episodes.watched)
    )

    fun toTraktList(response: TraktCreateListResponse) = Trakt_list(
        id = response.ids.trakt.toLong(),
        slug = response.ids.slug,
        description = response.description
    )

    fun toTraktList(
        slug: String,
        response: TraktUserResponse
    ) = Trakt_user(
        slug = response.ids.slug,
        full_name = response.name,
        user_name = response.userName,
        profile_picture = response.images.avatar.full,
        is_me = slug == "me"
    )

    fun responseToCache(

        response: List<TraktFollowedShowResponse>
    ) = response.map {
        Followed_shows(
            id = it.show.ids.trakt.toLong(),
            synced = true,
            created_at = dateFormatter.getTimestampMilliseconds()
        )
    }
}
