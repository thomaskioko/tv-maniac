package com.thomaskioko.tvmaniac.profilestats.implementation

import com.thomaskioko.tvmaniac.core.db.User_stats
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUserStatsResponse
import com.thomaskioko.tvmaniac.util.FormatterUtil
import me.tatarka.inject.annotations.Inject
import kotlin.math.roundToInt

@Inject
class StatsMapper(
    private val formatterUtil: FormatterUtil,
) {

    fun toTraktStats(
        slug: String,
        response: TraktUserStatsResponse,
    ) = User_stats(
        user_slug = slug,
        collected_shows = response.shows.collected.toString(),
        months = (response.episodes.minutes / 43800).toDouble().roundToInt().toString(),
        days = (response.episodes.minutes / 1440).toDouble().roundToInt().toString(),
        hours = formatterUtil.formatDuration(response.episodes.minutes / 60),
        episodes_watched = formatterUtil.formatDuration(response.episodes.watched),
    )
}

