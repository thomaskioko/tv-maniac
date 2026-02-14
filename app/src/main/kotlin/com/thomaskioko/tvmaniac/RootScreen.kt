package com.thomaskioko.tvmaniac

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
import com.thomaskioko.tvmaniac.debug.ui.DebugMenuScreen
import com.thomaskioko.tvmaniac.home.ui.HomeScreen
import com.thomaskioko.tvmaniac.moreshows.ui.MoreShowsScreen
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
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
        when (val screen = child.instance) {
            is RootPresenter.Child.Home ->
                HomeScreen(presenter = screen.presenter, modifier = fillMaxSizeModifier)

            is RootPresenter.Child.Profile ->
                ProfileScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )

            is RootPresenter.Child.Settings ->
                SettingsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            is RootPresenter.Child.Debug ->
                DebugMenuScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )

            is RootPresenter.Child.ShowDetails -> {
                ShowDetailsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }

            is RootPresenter.Child.SeasonDetails -> {
                SeasonDetailsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }

            is RootPresenter.Child.Trailers ->
                TrailersScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )

            is RootPresenter.Child.MoreShows ->
                MoreShowsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )

            RootPresenter.Child.GenreShows -> {
                // TODO:: Genre Shows Screen
            }
        }
    }
}
