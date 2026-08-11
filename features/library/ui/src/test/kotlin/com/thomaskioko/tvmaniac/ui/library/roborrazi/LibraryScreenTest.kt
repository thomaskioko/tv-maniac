package com.thomaskioko.tvmaniac.ui.library.roborrazi

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.TvManiacBackground
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.screenshottests.captureMultiDevice
import com.thomaskioko.tvmaniac.ui.library.LayoutMenu
import com.thomaskioko.tvmaniac.ui.library.LibraryScreen
import com.thomaskioko.tvmaniac.ui.library.preview.LibraryStatePreviewParameterProvider
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
class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val states = LibraryStatePreviewParameterProvider().values.toList()

    @Test
    fun libraryScreenLoadingState() {
        composeTestRule.captureMultiDevice("LibraryLoadingState") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[0],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenEmptyState() {
        composeTestRule.captureMultiDevice("LibraryEmptyState") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[1],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenGridMode() {
        composeTestRule.captureMultiDevice("LibraryGridMode") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[2],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenListModeWithFilters() {
        composeTestRule.captureMultiDevice("LibraryListModeWithFilters") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[3],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenCompactMode() {
        composeTestRule.captureMultiDevice("LibraryCompactMode") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[4],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenDetailedMode() {
        composeTestRule.captureMultiDevice("LibraryDetailedMode") {
            TvManiacBackground {
                LibraryScreen(
                    state = states[5],
                    onAction = {},
                )
            }
        }
    }

    @Test
    fun libraryScreenLayoutMenuUnlocked() {
        composeTestRule.captureMultiDevice("LibraryLayoutMenuUnlocked") {
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
    fun libraryScreenLayoutMenuLocked() {
        composeTestRule.captureMultiDevice("LibraryLayoutMenuLocked") {
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
