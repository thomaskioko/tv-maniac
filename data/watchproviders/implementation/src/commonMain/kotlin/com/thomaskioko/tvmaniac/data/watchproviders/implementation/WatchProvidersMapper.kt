package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.Watch_providers
import com.thomaskioko.tvmaniac.tmdb.api.model.US
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.Inject

@Inject
public class WatchProvidersMapper(
    private val formatterUtil: FormatterUtil,
) {

    public fun mapToRows(
        us: US,
        tmdbId: Long,
        traktId: Long,
    ): List<Watch_providers> {
        return us.flatrate
            .map { ProviderRow(it.providerId, it.providerName, it.logoPath) }
            .dedupedByBrand { it.providerName }
            .map { row ->
                Watch_providers(
                    id = Id(row.providerId.toLong()),
                    tmdb_id = Id(tmdbId),
                    trakt_id = Id(traktId),
                    logo_path = row.logoPath?.let { formatterUtil.formatTmdbPosterPath(it) },
                    name = row.providerName,
                )
            }
    }

    private data class ProviderRow(
        val providerId: Int,
        val providerName: String,
        val logoPath: String?,
    )
}
