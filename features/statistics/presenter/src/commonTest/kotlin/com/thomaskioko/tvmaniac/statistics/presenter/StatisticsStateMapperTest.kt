package com.thomaskioko.tvmaniac.statistics.presenter

import com.thomaskioko.tvmaniac.domain.statistics.model.DailyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.MonthlyWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.WatchStatistics
import com.thomaskioko.tvmaniac.domain.statistics.model.WeekdayWatchCount
import com.thomaskioko.tvmaniac.domain.statistics.model.YearlyWatchCount
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.FakeLocalizer
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlin.test.Test

internal class StatisticsStateMapperTest {

    private val localizer = FakeLocalizer()

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
    fun `should scale heat map levels against the busiest day`() {
        val heatMap = mapper.toHeatMap(statisticsWithDailyCounts()).shouldNotBeNull()

        heatMap.cells.map { it.level } shouldBe listOf(0, 2, 4)
        heatMap.cells.map { it.episodeCount } shouldBe listOf(0, 2, 4)
        heatMap.cells.map { it.date } shouldBe listOf("2026-08-01", "2026-08-02", "2026-08-03")
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

        bars.map { it.episodeCount } shouldBe listOf(1, 0, 4)
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
        bars.filter { it.episodeCount == 0 }.size shouldBe 5
    }

    @Test
    fun `should carry the yearly bars up to this year given the last watch was earlier`() {
        dateTimeProvider.setCurrentYear(2026)
        val statistics = WatchStatistics.EMPTY.copy(
            yearlyCounts = listOf(YearlyWatchCount(year = 2023, episodeCount = 4, minutes = 120)),
        )

        val bars = mapper.toYearlyActivity(statistics)

        bars.map { it.label } shouldBe listOf("2023", "2024", "2025", "2026")
        bars.map { it.episodeCount } shouldBe listOf(4, 0, 0, 0)
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

    private fun statisticsWithDailyCounts(): WatchStatistics = WatchStatistics.EMPTY.copy(
        dailyCounts = listOf(
            DailyWatchCount(date = LocalDate(2026, 8, 1), episodeCount = 0, minutes = 0),
            DailyWatchCount(date = LocalDate(2026, 8, 2), episodeCount = 2, minutes = 60),
            DailyWatchCount(date = LocalDate(2026, 8, 3), episodeCount = 4, minutes = 120),
        ),
    )
}
