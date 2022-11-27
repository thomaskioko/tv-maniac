import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.test.runBlockingTest
import com.thomaskioko.tvmaniac.shows.api.FetchShows
import com.thomaskioko.tvmaniac.shows.api.LoadShows
import com.thomaskioko.tvmaniac.shows.api.ShowUpdateState
import com.thomaskioko.tvmaniac.shows.api.ShowsLoaded
import com.thomaskioko.tvmaniac.shows.api.ShowsStateMachine
import com.thomaskioko.tvmaniac.shows.api.showResult
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbRepository
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktRepository
import io.kotest.matchers.shouldBe
import kotlin.test.Test

internal class ShowsStateMachineTest {

    private val traktRepository = FakeTraktRepository()
    private val tmdbRepository = FakeTmdbRepository()
    private val stateMachine = ShowsStateMachine(traktRepository, tmdbRepository)

    @Test
    fun initial_state_emits_expected_result() = runBlockingTest {

        stateMachine.state.test {
            awaitItem() shouldBe FetchShows
            awaitItem() shouldBe LoadShows
            awaitItem() shouldBe ShowsLoaded(
                result = showResult.copy(
                    updateState = ShowUpdateState.IDLE
                )
            )
            awaitItem() shouldBe ShowsLoaded(
                result = showResult.copy(
                    updateState = ShowUpdateState.IDLE
                )
            )
        }
    }
}
