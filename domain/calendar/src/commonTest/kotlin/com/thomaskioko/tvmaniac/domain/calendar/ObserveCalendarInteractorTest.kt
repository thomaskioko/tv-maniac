package com.thomaskioko.tvmaniac.domain.calendar

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.testing.FakeCalendarRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test

internal class ObserveCalendarInteractorTest {

    private val calendarRepository = FakeCalendarRepository()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val formatterUtil = FakeFormatterUtil()

    @Test
    fun `should return empty list given no entries`() = runTest {
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            awaitItem().shouldBeEmpty()
        }
    }

    @Test
    fun `should group entries by date given entries on same day`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = airDate),
                createTestEntry(showTraktId = 2, episodeTraktId = 20, airDate = airDate),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups shouldHaveSize 1
            groups[0].episodes shouldHaveSize 2
        }
    }

    @Test
    fun `should create separate groups given entries on different days`() = runTest {
        val todayEpoch = todayEpochMillis()
        val tomorrowEpoch = tomorrowEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = todayEpoch),
                createTestEntry(showTraktId = 2, episodeTraktId = 20, airDate = tomorrowEpoch),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups shouldHaveSize 2
            groups[0].episodes shouldHaveSize 1
            groups[1].episodes shouldHaveSize 1
        }
    }

    @Test
    fun `should group multiple episodes of the same show given same date`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 1, episodeTraktId = 10, seasonNumber = 1, episodeNumber = 1, airDate = airDate),
                createTestEntry(showTraktId = 1, episodeTraktId = 11, seasonNumber = 1, episodeNumber = 2, airDate = airDate),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups shouldHaveSize 1
            groups[0].episodes shouldHaveSize 1
            groups[0].episodes[0].additionalEpisodesCount shouldBe 1
        }
    }

    @Test
    fun `should format episode info with title given title is present`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 2,
                    episodeNumber = 5,
                    episodeTitle = "The One",
                    airDate = airDate,
                ),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups[0].episodes[0].episodeInfo shouldBe "S02E05 · The One"
        }
    }

    @Test
    fun `should format episode info without title given title is null`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 1,
                    episodeNumber = 3,
                    episodeTitle = null,
                    airDate = airDate,
                ),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups[0].episodes[0].episodeInfo shouldBe "S01E03"
        }
    }

    @Test
    fun `should map show metadata to grouped episode entry`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    showTitle = "Breaking Bad",
                    showPosterPath = "/poster.jpg",
                    network = "AMC",
                    overview = "A great episode",
                    rating = 9.5,
                    votes = 1000,
                    runtime = 45,
                    airDate = airDate,
                ),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val episode = awaitItem()[0].episodes[0]
            episode.showTraktId shouldBe 1
            episode.episodeTraktId shouldBe 10
            episode.showTitle shouldBe "Breaking Bad"
            episode.posterUrl shouldBe "/poster.jpg"
            episode.network shouldBe "AMC"
            episode.overview shouldBe "A great episode"
            episode.rating shouldBe 9.5
            episode.votes shouldBe 1000
            episode.runtime shouldBe 45
        }
    }

    @Test
    fun `should sort date groups chronologically given multiple dates`() = runTest {
        val todayEpoch = todayEpochMillis()
        val tomorrowEpoch = tomorrowEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(showTraktId = 2, episodeTraktId = 20, airDate = tomorrowEpoch),
                createTestEntry(showTraktId = 1, episodeTraktId = 10, airDate = todayEpoch),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val groups = awaitItem()
            groups shouldHaveSize 2
            groups[0].episodes[0].showTraktId shouldBe 1
            groups[1].episodes[0].showTraktId shouldBe 2
        }
    }

    @Test
    fun `should use first episode when grouping by show given multiple episodes`() = runTest {
        val airDate = todayEpochMillis()
        calendarRepository.setCalendarEntries(
            listOf(
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 11,
                    seasonNumber = 1,
                    episodeNumber = 2,
                    episodeTitle = "Second",
                    airDate = airDate,
                ),
                createTestEntry(
                    showTraktId = 1,
                    episodeTraktId = 10,
                    seasonNumber = 1,
                    episodeNumber = 1,
                    episodeTitle = "First",
                    airDate = airDate,
                ),
            ),
        )
        val interactor = createInteractor()
        val (startEpoch, endEpoch) = epochRange()

        interactor(ObserveCalendarInteractor.Params(startEpoch, endEpoch))

        interactor.flow.test {
            val episode = awaitItem()[0].episodes[0]
            episode.episodeTraktId shouldBe 10
            episode.episodeInfo shouldBe "S01E01 · First"
            episode.additionalEpisodesCount shouldBe 1
        }
    }

    private fun createInteractor(): ObserveCalendarInteractor {
        val calendarWeekCalculator = CalendarWeekCalculator(
            dateTimeProvider = dateTimeProvider,
            formatterUtil = formatterUtil,
        )
        val calendarEpisodeFormatter = CalendarEpisodeFormatter(
            formatterUtil = formatterUtil,
        )
        return ObserveCalendarInteractor(
            repository = calendarRepository,
            calendarWeekCalculator = calendarWeekCalculator,
            calendarEpisodeFormatter = calendarEpisodeFormatter,
            dateTimeProvider = dateTimeProvider,
        )
    }

    private fun todayEpochMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    private fun tomorrowEpochMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        return tomorrow.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    private fun epochRange(): Pair<Long, Long> {
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val endDate = today.plus(CalendarWeekCalculator.DAYS_IN_WEEK, DateTimeUnit.DAY)
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds() to
            endDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    @Suppress("LongParameterList")
    private fun createTestEntry(
        showTraktId: Long = 1,
        episodeTraktId: Long = 10,
        seasonNumber: Int = 1,
        episodeNumber: Int = 1,
        episodeTitle: String? = "Test Episode",
        airDate: Long = 0L,
        showTitle: String = "Test Show",
        showPosterPath: String? = null,
        network: String? = "HBO",
        runtime: Int? = 60,
        overview: String? = "Test overview",
        rating: Double? = 8.0,
        votes: Int? = 100,
    ): CalendarEntry = CalendarEntry(
        showTraktId = showTraktId,
        episodeTraktId = episodeTraktId,
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        episodeTitle = episodeTitle,
        airDate = airDate,
        showTitle = showTitle,
        showPosterPath = showPosterPath,
        network = network,
        runtime = runtime,
        overview = overview,
        rating = rating,
        votes = votes,
    )
}
