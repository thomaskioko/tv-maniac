package com.thomaskioko.tvmaniac.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.compose.components.NotificationRationaleContent
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.debug.ui.DebugMenuScreen
import com.thomaskioko.tvmaniac.episodedetail.ui.EpisodeSheet
import com.thomaskioko.tvmaniac.home.ui.HomeScreen
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.moreshows.ui.MoreShowsScreen
import com.thomaskioko.tvmaniac.navigation.ScreenDestination
import com.thomaskioko.tvmaniac.navigation.SheetDestination
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.search.ui.SearchScreen
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.ui.SettingsScreen
import com.thomaskioko.tvmaniac.showdetails.ui.ShowDetailsScreen
import com.thomaskioko.tvmaniac.trailers.ui.TrailersScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun RootScreen(
    rootPresenter: RootPresenter,
    modifier: Modifier = Modifier,
) {
    val notificationPermissionState by rootPresenter.notificationPermissionState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        rootPresenter.onNotificationPermissionResult(granted)
    }

    if (notificationPermissionState.showRationale) {
        ModalBottomSheet(
            onDismissRequest = { rootPresenter.onRationaleDismissed() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            NotificationRationaleContent(
                onEnable = { rootPresenter.onRationaleAccepted() },
                onDismiss = { rootPresenter.onRationaleDismissed() },
            )
        }
    }

    LaunchedEffect(notificationPermissionState.requestPermission) {
        if (notificationPermissionState.requestPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                rootPresenter.onNotificationPermissionResult(true)
            }
        }
    }

    val episodeSheetSlot by rootPresenter.episodeSheetSlot.collectAsStateWithLifecycle()
    (episodeSheetSlot.child?.instance as? SheetDestination<*>)?.let { child ->
        (child.presenter as? EpisodeSheetPresenter)?.let { presenter ->
            EpisodeSheet(presenter = presenter)
        }
    }

    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        ) {
            ChildrenContent(rootPresenter = rootPresenter, modifier = Modifier.weight(1F))
        }
    }
}

@Composable
private fun ChildrenContent(rootPresenter: RootPresenter, modifier: Modifier = Modifier) {
    val childStack by rootPresenter.childStack.collectAsStateWithLifecycle()

    Children(
        modifier = modifier,
        stack = childStack,
    ) { child ->
        val fillMaxSizeModifier = Modifier.fillMaxSize()
        when (val presenter = (child.instance as? ScreenDestination<*>)?.presenter) {
            is HomePresenter ->
                HomeScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is SearchShowsPresenter ->
                SearchScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is SettingsPresenter ->
                SettingsScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is DebugPresenter ->
                DebugMenuScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is ShowDetailsPresenter ->
                ShowDetailsScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is SeasonDetailsPresenter ->
                SeasonDetailsScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is TrailersPresenter ->
                TrailersScreen(presenter = presenter, modifier = fillMaxSizeModifier)

            is MoreShowsPresenter ->
                MoreShowsScreen(presenter = presenter, modifier = fillMaxSizeModifier)
        }
    }
}
