package com.thomaskioko.tvmaniac.myshows.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.ui.LayoutMenu
import com.thomaskioko.tvmaniac.myshows.ui.MyShowsScreen
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import kotlinx.collections.immutable.persistentListOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

private val TopBarAnchorInset = 120.dp

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@LooperMode(LooperMode.Mode.PAUSED)
class MyShowsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun myShowsPagerContinueWatchingTab() {
        composeTestRule.captureMultiDevice("MyShowsPagerContinueWatching") {
            TvManiacBackground {
                MyShowsScreen(
                    state = MyShowsState(
                        continueWatchingTitle = "Continue Watching",
                        startWatchingTitle = "Start Watching",
                    ),
                    tabs = persistentListOf("Continue Watching", "Start Watching"),
                    continueWatchingContent = { },
                    startWatchingContent = { },
                )
            }
        }
    }

    @Test
    fun myShowsLayoutMenuUnlocked() {
        composeTestRule.captureMultiDevice("MyShowsLayoutMenuUnlocked") {
            TvManiacBackground {
                Box(modifier = Modifier.padding(top = TopBarAnchorInset)) {
                    LayoutMenu(
                        currentStyle = ListStyle.GRID,
                        isLocked = false,
                        expanded = true,
                        onExpandedChange = {},
                        onAction = {},
                    )
                }
            }
        }
    }

    @Test
    fun myShowsLayoutMenuLocked() {
        composeTestRule.captureMultiDevice("MyShowsLayoutMenuLocked") {
            TvManiacBackground {
                Box(modifier = Modifier.padding(top = TopBarAnchorInset)) {
                    LayoutMenu(
                        currentStyle = ListStyle.LIST,
                        isLocked = true,
                        expanded = true,
                        onExpandedChange = {},
                        onAction = {},
                    )
                }
            }
        }
    }
}
