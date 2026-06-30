package com.thomaskioko.tvmaniac.discover.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import com.thomaskioko.tvmaniac.compose.util.LocalAutoAdvanceEnabled
import com.thomaskioko.tvmaniac.discover.ui.component.PosterCardsPager
import io.kotest.matchers.floats.shouldBeExactly
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class PosterCardsPagerTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `should settle pager to nearest page given restored fractional offset`() {
        val shows = discoverFeaturedContentSuccess.featuredShows
        lateinit var pagerState: PagerState

        composeTestRule.setContent {
            CompositionLocalProvider(LocalAutoAdvanceEnabled provides false) {
                pagerState = rememberPagerState(
                    initialPage = 2,
                    initialPageOffsetFraction = 0.3f,
                    pageCount = { shows.size },
                )
                PosterCardsPager(
                    pagerState = pagerState,
                    list = shows,
                    onClick = {},
                )
            }
        }

        composeTestRule.runOnIdle {
            pagerState.currentPageOffsetFraction shouldBeExactly 0f
        }
    }
}
