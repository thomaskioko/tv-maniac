package com.thomaskioko.tvmaniac.app.test.compose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityOptionsCompat
import com.thomaskioko.tvmaniac.app.di.ActivityGraph
import com.thomaskioko.tvmaniac.app.test.TvManiacTestApplication
import com.thomaskioko.tvmaniac.app.ui.di.AppRootContent
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

        val resultRegistryOwner = DocumentPickerCancellingRegistryOwner(this)
        setContent {
            val appUiState by activityGraph.rootPresenter.appUiState.collectAsState()
            TvManiacTheme(appTheme = appUiState.appTheme) {
                CompositionLocalProvider(
                    LocalAutoAdvanceEnabled provides false,
                    LocalActivityResultRegistryOwner provides resultRegistryOwner,
                ) {
                    activityGraph.AppRootContent()
                }
            }
        }
    }
}

private class DocumentPickerCancellingRegistryOwner(
    private val activity: ComponentActivity,
) : ActivityResultRegistryOwner {

    override val activityResultRegistry: ActivityResultRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?,
        ) {
            when (contract) {
                is ActivityResultContracts.OpenDocument,
                is ActivityResultContracts.OpenDocumentTree,
                -> dispatchResult(requestCode, Activity.RESULT_CANCELED, null)

                else -> {
                    var launcher: ActivityResultLauncher<I>? = null
                    launcher = activity.activityResultRegistry.register("forwarded-$requestCode", contract) { result ->
                        dispatchResult(requestCode, result)
                        launcher?.unregister()
                    }
                    launcher.launch(input, options)
                }
            }
        }
    }
}
