package com.thomaskioko.tvmaniac.statistics.presenter

import com.thomaskioko.tvmaniac.domain.statistics.model.DailyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.GenreCount
import com.thomaskioko.tvmaniac.domain.statistics.model.MonthlyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.ReleaseYearCount
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStatistics
import com.thomaskioko.tvmaniac.domain.statistics.model.WeekdayWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.YearlyWatchCount
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlin.test.Test

internal class StatisticsStateMapperTest {

    private val localizer = TestLocalizer()

    private val dateTimeProvider = FakeDateTimeProvider()

    private val mapper = StatisticsStateMapper(
        localizer = localizer,
        formatterUtil = FakeFormatterUtil(),
        dateTimeProvider = dateTimeProvider,
    )

    @Test
    fun `should return no heat map given no daily counts`() {
        mapper.toHeatMap(WatchStatistics.EMPTY) shouldBe null
    }

    @Test
    fun `should shade heat map days by how many episodes they carry`() {
        val heatMap = mapper.toHeatMap(statisticsWithDailyCounts()).shouldNotBeNull()

        heatMap.cells.map { it.level } shouldBe listOf(0, 1, 2)
        heatMap.cells.map { it.episodeCount } shouldBe listOf(0, 2, 4)
        heatMap.cells.map { it.date } shouldBe listOf("2026-08-01", "2026-08-02", "2026-08-03")
    }

    @Test
    fun `should keep ordinary days readable given one import day carries hundreds of episodes`() {
        val statistics = WatchStatistics.EMPTY.copy(
            dailyCounts = List(20) { index ->
                DailyWatchCount(
                    date = LocalDate(2026, 8, 1).plus(index, DateTimeUnit.DAY),
                    episodeCount = if (index == 0) 285 else 3,
                    minutes = 60,
                )
            },
        )

        val heatMap = mapper.toHeatMap(statistics).shouldNotBeNull()

        heatMap.cells.first().level shouldBe 4
        heatMap.cells.drop(1).map { it.level }.toSet() shouldBe setOf(2)
    }

    @Test
    fun `should outline today rather than the last day in the series`() {
        dateTimeProvider.setFakeToday(year = 2026, month = 8, day = 2)

        val heatMap = mapper.toHeatMap(statisticsWithDailyCounts()).shouldNotBeNull()

        heatMap.cells.filter { it.isToday }.map { it.date } shouldBe listOf("2026-08-02")
    }

    @Test
    fun `should split the heat map into active and quiet days`() {
        val heatMap = mapper.toHeatMap(statisticsWithDailyCounts()).shouldNotBeNull()

        heatMap.activeDays shouldBe 2
        heatMap.quietDays shouldBe 1
    }

    @Test
    fun `should pad the heat map so the first column lands on its weekday`() {
        val heatMap = mapper.toHeatMap(statisticsWithDailyCounts()).shouldNotBeNull()

        heatMap.leadingBlankCells shouldBe 6
    }

    @Test
    fun `should label the yearly bars and scale them against the busiest year`() {
        val statistics = WatchStatistics.EMPTY.copy(
            yearlyCounts = listOf(
                YearlyWatchCount(year = 2024, episodeCount = 1, minutes = 30),
                YearlyWatchCount(year = 2026, episodeCount = 4, minutes = 120),
            ),
        )

        val bars = mapper.toYearlyActivity(statistics)

        bars.map { it.count } shouldBe listOf(1, 0, 4)
        bars.map { it.fraction } shouldBe listOf(0.25f, 0f, 1f)
    }

    @Test
    fun `should give a bar to every year between the first and last watch`() {
        val statistics = WatchStatistics.EMPTY.copy(
            yearlyCounts = listOf(
                YearlyWatchCount(year = 2020, episodeCount = 2, minutes = 60),
                YearlyWatchCount(year = 2026, episodeCount = 4, minutes = 120),
            ),
        )

        val bars = mapper.toYearlyActivity(statistics)

        bars.map { it.label } shouldBe listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026")
        bars.filter { it.count == 0 }.size shouldBe 5
    }

    @Test
    fun `should carry the yearly bars up to this year given the last watch was earlier`() {
        dateTimeProvider.setCurrentYear(2026)
        val statistics = WatchStatistics.EMPTY.copy(
            yearlyCounts = listOf(YearlyWatchCount(year = 2023, episodeCount = 4, minutes = 120)),
        )

        val bars = mapper.toYearlyActivity(statistics)

        bars.map { it.label } shouldBe listOf("2023", "2024", "2025", "2026")
        bars.map { it.count } shouldBe listOf(4, 0, 0, 0)
    }

    @Test
    fun `should give each bar a caption naming its episode count`() {
        val statistics = WatchStatistics.EMPTY.copy(
            yearlyCounts = listOf(YearlyWatchCount(year = 2026, episodeCount = 4, minutes = 120)),
        )

        mapper.toYearlyActivity(statistics).single().caption shouldBe
            localizer.getPlural(PluralsResourceKey.EpisodeCount, 4)
    }

    @Test
    fun `should give no yearly bars given nothing was watched`() {
        mapper.toYearlyActivity(WatchStatistics.EMPTY).size shouldBe 0
    }

    @Test
    fun `should label the monthly bars with the month name`() {
        val statistics = WatchStatistics.EMPTY.copy(
            monthlyCounts = listOf(
                MonthlyWatchCount(month = Month.JANUARY, episodeCount = 1, minutes = 30),
                MonthlyWatchCount(month = Month.AUGUST, episodeCount = 2, minutes = 60),
            ),
        )

        val bars = mapper.toMonthlyActivity(statistics)

        bars.map { it.label } shouldBe listOf("JANUARY", "AUGUST")
        bars.map { it.fraction } shouldBe listOf(0.5f, 1f)
    }

    @Test
    fun `should label the weekday bars with the weekday name`() {
        val statistics = WatchStatistics.EMPTY.copy(
            weekdayCounts = listOf(
                WeekdayWatchCount(dayOfWeek = DayOfWeek.MONDAY, episodeCount = 2, minutes = 60),
                WeekdayWatchCount(dayOfWeek = DayOfWeek.SATURDAY, episodeCount = 1, minutes = 30),
            ),
        )

        val bars = mapper.toWeekdayActivity(statistics)

        bars.map { it.label } shouldBe listOf("MONDAY", "SATURDAY")
        bars.map { it.fraction } shouldBe listOf(1f, 0.5f)
    }

    @Test
    fun `should give every bar no width given nothing was watched`() {
        val statistics = WatchStatistics.EMPTY.copy(
            weekdayCounts = DayOfWeek.entries.map {
                WeekdayWatchCount(dayOfWeek = it, episodeCount = 0, minutes = 0)
            },
        )

        mapper.toWeekdayActivity(statistics).map { it.fraction } shouldBe List(7) { 0f }
    }

    @Test
    fun `should build a tile for the current streak and the days watched this month`() {
        val ids = mapper.toTiles(WatchStatistics.EMPTY).map { it.id }

        ids.contains(StatisticTileId.CurrentStreak) shouldBe true
        ids.contains(StatisticTileId.WatchDaysThisMonth) shouldBe true
    }

    @Test
    fun `should return no genre slices given no genres`() {
        mapper.toGenreBreakdown(WatchStatistics.EMPTY).isEmpty() shouldBe true
    }

    @Test
    fun `should scale each genre against the most watched one`() {
        val statistics = WatchStatistics.EMPTY.copy(
            genreBreakdown = listOf(
                GenreCount(name = "Drama", showCount = 3),
                GenreCount(name = "Crime", showCount = 1),
            ),
        )

        val slices = mapper.toGenreBreakdown(statistics)

        slices.map { it.name } shouldBe listOf("Drama", "Crime")
        slices.map { it.showCount } shouldBe listOf(3, 1)
        slices.map { it.fraction } shouldBe listOf(1f, 1f / 3f)
    }

    @Test
    fun `should roll the genres past the sixth into one other row`() {
        val statistics = WatchStatistics.EMPTY.copy(
            genreBreakdown = (1..9).map { GenreCount(name = "genre-$it", showCount = 10L - it) },
        )

        val slices = mapper.toGenreBreakdown(statistics)

        slices.size shouldBe 7
        slices.take(6).map { it.name } shouldBe (1..6).map { "genre-$it" }
        slices.last().name shouldBe localizer.getString(StringResourceKey.LabelStatisticsGenreOther)
        slices.last().showCount shouldBe 6
    }

    @Test
    fun `should leave out the other row given six genres or fewer`() {
        val statistics = WatchStatistics.EMPTY.copy(
            genreBreakdown = (1..6).map { GenreCount(name = "genre-$it", showCount = 1) },
        )

        mapper.toGenreBreakdown(statistics).size shouldBe 6
    }

    @Test
    fun `should caption the release year bars with a show count`() {
        val statistics = WatchStatistics.EMPTY.copy(
            releaseYears = listOf(
                ReleaseYearCount(year = 2002, showCount = 1),
                ReleaseYearCount(year = 2008, showCount = 2),
            ),
        )

        val bars = mapper.toReleaseYears(statistics)

        bars.map { it.label } shouldBe listOf("2002", "2008")
        bars.map { it.count } shouldBe listOf(1, 2)
        bars.map { it.fraction } shouldBe listOf(0.5f, 1f)
        bars.first().caption shouldBe localizer.getPlural(PluralsResourceKey.ShowCount, 1)
    }

    private fun statisticsWithDailyCounts(): WatchStatistics = WatchStatistics.EMPTY.copy(
        dailyCounts = listOf(
            DailyWatchCount(date = LocalDate(2026, 8, 1), episodeCount = 0, minutes = 0),
            DailyWatchCount(date = LocalDate(2026, 8, 2), episodeCount = 2, minutes = 60),
            DailyWatchCount(date = LocalDate(2026, 8, 3), episodeCount = 4, minutes = 120),
        ),
    )
}
