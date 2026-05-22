package com.thomaskioko.tvmaniac.data.watchproviders.implementation

import com.thomaskioko.tvmaniac.tmdb.api.model.FlatRate
import com.thomaskioko.tvmaniac.tmdb.api.model.US
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class WatchProvidersMapperTest {

    private val mapper = WatchProvidersMapper(formatterUtil = PassThroughFormatterUtil())

    @Test
    fun `should collapse duplicate brands with distinct provider ids`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 1, providerName = "MGM+", logoPath = "/mgm.png"),
                FlatRate(providerId = 2, providerName = "MGM+", logoPath = "/mgm-2.png"),
                FlatRate(providerId = 3, providerName = "MGM+", logoPath = "/mgm-3.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf("MGM+")
        rows.single().id.id shouldBe 1L
    }

    @Test
    fun `should treat whitespace and case differences as the same brand`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 1, providerName = "MGM+", logoPath = "/a.png"),
                FlatRate(providerId = 2, providerName = "mgm+ ", logoPath = "/b.png"),
                FlatRate(providerId = 3, providerName = " MGM+", logoPath = "/c.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows shouldHaveSize 1
        rows.single().id.id shouldBe 1L
    }

    @Test
    fun `should treat plus suffix and Plus word as the same brand`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 1, providerName = "MGM+", logoPath = "/a.png"),
                FlatRate(providerId = 2, providerName = "MGM Plus", logoPath = "/b.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows shouldHaveSize 1
        rows.single().name shouldBe "MGM+"
    }

    @Test
    fun `should collapse MGM channel and tier variants under canonical MGM Plus`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 257, providerName = "fuboTV", logoPath = "/fubo.png"),
                FlatRate(providerId = 583, providerName = "MGM+ Amazon Channel", logoPath = "/mgm-amazon.png"),
                FlatRate(providerId = 636, providerName = "MGM Plus Roku Premium Channel", logoPath = "/mgm-roku.png"),
                FlatRate(providerId = 34, providerName = "MGM Plus", logoPath = "/mgm.png"),
                FlatRate(providerId = 2383, providerName = "Philo", logoPath = "/philo.png"),
                FlatRate(providerId = 486, providerName = "Spectrum On Demand", logoPath = "/spectrum.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf(
            "fuboTV",
            "MGM Plus",
            "Philo",
            "Spectrum On Demand",
        )
        rows.single { it.name == "MGM Plus" }.id.id shouldBe 34L
    }

    @Test
    fun `should collapse Amazon Prime Video tier variant under canonical brand`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(
                    providerId = 9,
                    providerName = "Amazon Prime Video",
                    logoPath = "/pvske1MyAoymrs5bguRfVqYiM9a.jpg",
                ),
                FlatRate(
                    providerId = 2100,
                    providerName = "Amazon Prime Video with Ads",
                    logoPath = "/8aBqoNeGGr0oSA85iopgNZUOTOc.jpg",
                ),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows shouldHaveSize 1
        rows.single().name shouldBe "Amazon Prime Video"
        rows.single().id.id shouldBe 9L
    }

    @Test
    fun `should collapse any with-suffix tier variant under canonical brand`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 15, providerName = "Hulu", logoPath = "/hulu.png"),
                FlatRate(providerId = 453, providerName = "Hulu with Live TV", logoPath = "/hulu-live.png"),
                FlatRate(providerId = 531, providerName = "Paramount Plus", logoPath = "/paramount.png"),
                FlatRate(
                    providerId = 1853,
                    providerName = "Paramount Plus with Showtime",
                    logoPath = "/paramount-showtime.png",
                ),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf("Hulu", "Paramount Plus")
    }

    @Test
    fun `should keep distinct brands even when they share a leading word`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 1, providerName = "Paramount Plus", logoPath = "/pp.png"),
                FlatRate(providerId = 2, providerName = "Paramount Network", logoPath = "/pn.png"),
                FlatRate(providerId = 3, providerName = "Disney Plus", logoPath = "/dp.png"),
                FlatRate(providerId = 4, providerName = "Discovery Plus", logoPath = "/disc.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf(
            "Paramount Plus",
            "Paramount Network",
            "Disney Plus",
            "Discovery Plus",
        )
    }

    @Test
    fun `should keep canonical even when variants appear before it in payload`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 583, providerName = "MGM+ Amazon Channel", logoPath = "/v1.png"),
                FlatRate(providerId = 636, providerName = "MGM Plus Roku Premium Channel", logoPath = "/v2.png"),
                FlatRate(providerId = 34, providerName = "MGM Plus", logoPath = "/canonical.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows shouldHaveSize 1
        rows.single().name shouldBe "MGM Plus"
        rows.single().id.id shouldBe 34L
    }

    @Test
    fun `should keep variant when canonical brand is absent from payload`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 583, providerName = "MGM+ Amazon Channel", logoPath = "/v1.png"),
                FlatRate(providerId = 636, providerName = "MGM Plus Roku Premium Channel", logoPath = "/v2.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf(
            "MGM+ Amazon Channel",
            "MGM Plus Roku Premium Channel",
        )
    }

    @Test
    fun `should keep distinct brands and dedupe only duplicates`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 100, providerName = "Netflix", logoPath = "/netflix.png"),
                FlatRate(providerId = 200, providerName = "MGM+", logoPath = "/mgm.png"),
                FlatRate(providerId = 201, providerName = "MGM+ Amazon Channel", logoPath = "/mgm-amazon.png"),
                FlatRate(providerId = 202, providerName = "MGM Plus Roku Premium Channel", logoPath = "/mgm-roku.png"),
                FlatRate(providerId = 300, providerName = "Disney+", logoPath = "/disney.png"),
                FlatRate(providerId = 301, providerName = "Disney+", logoPath = "/disney-dup.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.map { it.name } shouldContainExactly listOf("Netflix", "MGM+", "Disney+")
    }

    @Test
    fun `should return empty list when flatrate has no providers`() {
        val rows = mapper.mapToRows(US(), tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.shouldBeEmpty()
    }

    @Test
    fun `should stamp every row with tmdb id and trakt id`() {
        val us = US(
            flatrate = arrayListOf(
                FlatRate(providerId = 1, providerName = "Netflix", logoPath = "/a.png"),
                FlatRate(providerId = 2, providerName = "Disney+", logoPath = "/b.png"),
            ),
        )

        val rows = mapper.mapToRows(us, tmdbId = TMDB_ID, traktId = TRAKT_ID)

        rows.forEach { row ->
            row.tmdb_id.id shouldBe TMDB_ID
            row.trakt_id.id shouldBe TRAKT_ID
        }
    }

    private companion object {
        private const val TMDB_ID = 1396L
        private const val TRAKT_ID = 1388L
    }
}

private class PassThroughFormatterUtil : FormatterUtil {
    override fun formatTmdbPosterPath(imageUrl: String): String = imageUrl
    override fun formatDouble(number: Double?, scale: Int): Double = number ?: 0.0
    override fun formatDuration(number: Int): String = ""
    override fun formatDateTime(epochMillis: Long, pattern: String): String = ""
}
