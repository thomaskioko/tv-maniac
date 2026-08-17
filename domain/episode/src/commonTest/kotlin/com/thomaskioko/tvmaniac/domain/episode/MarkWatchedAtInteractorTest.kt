package com.thomaskioko.tvmaniac.domain.episode

import com.thomaskioko.tvmaniac.data.rewatch.testing.FakeRewatchRepository
import com.thomaskioko.tvmaniac.db.EpisodeById
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.domain.rewatch.WatchAgainInteractor
import com.thomaskioko.tvmaniac.episodes.api.WatchedDateTarget
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class MarkWatchedAtInteractorTest {

    private val episodeRepository = FakeEpisodeRepository()
    private val rewatchRepository = FakeRewatchRepository()
    private val dateTimeProvider = FakeDateTimeProvider().apply { setCurrentTimeMillis(NOW_MILLIS) }

    private val interactor = MarkWatchedAtInteractor(
        episodeRepository = episodeRepository,
        watchAgainInteractor = WatchAgainInteractor(rewatchRepository),
        dateTimeProvider = dateTimeProvider,
    )

    @Test
    fun `should mark the whole show given the target is a show`() = runTest {
        interactor.executeSync(
            MarkWatchedAtParams(
                target = WatchedDateTarget.SHOW,
                showId = SHOW_ID,
                watchedAt = PICKED_MILLIS,
            ),
        )

        val call = episodeRepository.lastMarkShowWatchedCall
        call?.showId shouldBe SHOW_ID
        call?.watchedAt shouldBe PICKED_MILLIS
        episodeRepository.lastMarkEpisodeWatchedCall.shouldBeNull()
    }

    @Test
    fun `should mark one season given the target is a season`() = runTest {
        interactor.executeSync(
            MarkWatchedAtParams(
                target = WatchedDateTarget.SEASON,
                showId = SHOW_ID,
                seasonNumber = 2L,
                watchedAt = PICKED_MILLIS,
            ),
        )

        val call = episodeRepository.lastMarkSeasonWatchedCall
        call?.seasonNumber shouldBe 2L
        call?.markPreviousSeasons shouldBe false
        call?.watchedAt shouldBe PICKED_MILLIS
    }

    @Test
    fun `should mark earlier seasons too given the season carries mark previous`() = runTest {
        interactor.executeSync(
            MarkWatchedAtParams(
                target = WatchedDateTarget.SEASON,
                showId = SHOW_ID,
                seasonNumber = 3L,
                markPrevious = true,
                watchedAt = PICKED_MILLIS,
            ),
        )

        episodeRepository.lastMarkSeasonWatchedCall?.markPreviousSeasons shouldBe true
    }

    @Test
    fun `should mark the episode given it was never watched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = false))

        interactor.executeSync(episodeParams(watchedAt = PICKED_MILLIS))

        val call = episodeRepository.lastMarkEpisodeWatchedCall
        call?.episodeId shouldBe EPISODE_ID
        call?.watchedAt shouldBe PICKED_MILLIS
        call?.markPreviousEpisodes shouldBe false
        rewatchRepository.lastAddEpisodeWatchedAt.shouldBeNull()
    }

    @Test
    fun `should mark earlier episodes too given the episode carries mark previous`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = false))

        interactor.executeSync(episodeParams(watchedAt = PICKED_MILLIS, markPrevious = true))

        episodeRepository.lastMarkEpisodeWatchedCall?.markPreviousEpisodes shouldBe true
    }

    @Test
    fun `should record a viewing given the episode is already watched`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        interactor.executeSync(episodeParams(watchedAt = PICKED_MILLIS))

        rewatchRepository.lastAddEpisodeWatchedAt shouldBe PICKED_MILLIS
        episodeRepository.lastMarkEpisodeWatchedCall.shouldBeNull()
    }

    @Test
    fun `should update the existing mark given the sheet is editing a watched episode`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        interactor.executeSync(episodeParams(watchedAt = PICKED_MILLIS, isEdit = true))

        episodeRepository.lastUpdateWatchedDateCall?.watchedAt shouldBe PICKED_MILLIS
        episodeRepository.lastMarkEpisodeWatchedCall.shouldBeNull()
        rewatchRepository.lastAddEpisodeWatchedAt.shouldBeNull()
    }

    @Test
    fun `should update at the release date given the sheet is editing and asks for it`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        interactor.executeSync(episodeParams(useReleaseDate = true, isEdit = true))

        episodeRepository.lastUpdateWatchedDateCall?.useReleaseDate shouldBe true
        episodeRepository.lastMarkEpisodeWatchedCall.shouldBeNull()
    }

    @Test
    fun `should record a viewing at the release date given a rewatch asks for it`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true))

        interactor.executeSync(episodeParams(useReleaseDate = true))

        rewatchRepository.lastAddEpisodeWatchedAt shouldBe AIR_DATE_MILLIS + RUNTIME_MINUTES * 60_000L
    }

    @Test
    fun `should record a viewing now given a rewatch has no date to work from`() = runTest {
        episodeRepository.setEpisodeById(testEpisode(isWatched = true, firstAired = null))

        interactor.executeSync(episodeParams(useReleaseDate = true))

        rewatchRepository.lastAddEpisodeWatchedAt shouldBe NOW_MILLIS
    }

    private fun episodeParams(
        watchedAt: Long? = null,
        useReleaseDate: Boolean = false,
        markPrevious: Boolean = false,
        isEdit: Boolean = false,
    ) = MarkWatchedAtParams(
        target = WatchedDateTarget.EPISODE,
        showId = SHOW_ID,
        episodeId = EPISODE_ID,
        seasonNumber = 1L,
        episodeNumber = 1L,
        markPrevious = markPrevious,
        isEdit = isEdit,
        watchedAt = watchedAt,
        useReleaseDate = useReleaseDate,
    )

    private fun testEpisode(
        isWatched: Boolean,
        firstAired: Long? = AIR_DATE_MILLIS,
    ) = EpisodeById(
        episode_id = Id(EPISODE_ID),
        season_id = Id(10L),
        show_id = Id(SHOW_ID),
        episode_number = 1L,
        title = "The Pilot",
        overview = "A chemistry teacher begins cooking meth.",
        vote_count = 1000L,
        ratings = 9.5,
        image_url = "https://image.url/episode.jpg",
        runtime = RUNTIME_MINUTES,
        first_aired = firstAired,
        season_number = 1L,
        show_name = "Breaking Bad",
        is_watched = if (isWatched) 1L else 0L,
        watched_at = null,
    )

    private companion object {
        private const val SHOW_ID = 100L
        private const val EPISODE_ID = 1L
        private const val RUNTIME_MINUTES = 45L
        private const val NOW_MILLIS = 1_755_252_000_000L
        private const val PICKED_MILLIS = 1_736_000_000_000L
        private const val AIR_DATE_MILLIS = 1_735_000_000_000L
    }
}
