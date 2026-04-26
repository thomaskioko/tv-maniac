package com.thomaskioko.tvmaniac.app.test.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.app.test.TvManiacTestApplication
import com.thomaskioko.tvmaniac.app.ui.RootScreen
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
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
            TvManiacTheme {
                RootScreen(
                    rootPresenter = activityGraph.rootPresenter,
                    screenContents = activityGraph.screenContents,
                    sheetContents = activityGraph.sheetContents,
                )
            }
        }
    }
}
