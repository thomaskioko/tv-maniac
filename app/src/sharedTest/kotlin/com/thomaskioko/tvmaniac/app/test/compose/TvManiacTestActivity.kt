package com.thomaskioko.tvmaniac.app.test.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.app.test.TvManiacTestApplication
import com.thomaskioko.tvmaniac.app.ui.RootScreen
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.util.LocalAutoAdvanceEnabled
import dev.zacsweers.metro.asContribution

internal class TvManiacTestActivity : ComponentActivity() {

    lateinit var activityGraph: ActivityGraph
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGraph = (application as TvManiacTestApplication).graph
            .asContribution<ActivityGraph.Factory>()
            .createGraph(this)

        setContent {
            val themeState by activityGraph.rootPresenter.themeState.collectAsState()
            TvManiacTheme(appTheme = themeState.appTheme) {
                CompositionLocalProvider(LocalAutoAdvanceEnabled provides false) {
                    RootScreen(
                        rootPresenter = activityGraph.rootPresenter,
                        screenContents = activityGraph.screenContents,
                        sheetContents = activityGraph.sheetContents,
                    )
                }
            }
        }
    }
}
