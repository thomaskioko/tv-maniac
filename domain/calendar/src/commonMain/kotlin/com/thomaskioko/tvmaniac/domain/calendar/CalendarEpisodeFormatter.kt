package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import me.tatarka.inject.annotations.Inject

@Inject
public class CalendarEpisodeFormatter(
    private val formatterUtil: FormatterUtil,
) {

    public fun formatEpisodeInfo(
        seasonNumber: Int,
        episodeNumber: Int,
        episodeTitle: String?,
    ): String {
        val seasonStr = seasonNumber.toString().padStart(2, '0')
        val episodeStr = episodeNumber.toString().padStart(2, '0')
        val title = episodeTitle ?: ""
        return if (title.isNotBlank()) {
            "S${seasonStr}E$episodeStr · $title"
        } else {
            "S${seasonStr}E$episodeStr"
        }
    }

    public fun formatAirTime(epochMillis: Long): String? {
        return runCatching {
            formatterUtil.formatDateTime(epochMillis, "HH:mm")
        }.getOrNull()
    }

    public fun formatFullAirDate(epochMillis: Long): String? {
        return runCatching {
            formatterUtil.formatDateTime(epochMillis, "EEEE, MMMM d, yyyy 'at' HH:mm")
        }.getOrNull()
    }
}
