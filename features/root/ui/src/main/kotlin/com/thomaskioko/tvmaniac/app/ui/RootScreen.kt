package com.thomaskioko.tvmaniac.app.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.router.slot.ChildSlot
import com.thomaskioko.root.model.NotificationPermissionState
import com.thomaskioko.tvmaniac.compose.components.NotificationRationaleContent
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.home.ui.HomeScreen
import com.thomaskioko.tvmaniac.navigation.SheetChild
import com.thomaskioko.tvmaniac.navigation.ui.LocalScreenContents
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.navigation.ui.SheetContent
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.presenter.root.model.ToastState
import com.thomaskioko.tvmaniac.presenter.root.model.ToastType
import io.github.thomaskioko.codegen.annotations.AppRootUi

/**
 * Activity-level host composable. Receives the navigation multibinding sets from the activity
 * graph, publishes [ScreenContent] through [LocalScreenContents] for descendants, and renders
 * [HomeScreen] directly.
 *
 * Annotated with [AppRootUi] so the codegen processor emits the `AppRootProvider` interface and
 * the `AppRootContent` extension that lets the activity collapse the multi-argument call into
 * `graph.AppRootContent()`.
 *
 * @param rootPresenter activity-scope root presenter from the dependency injection graph.
 * @param screenContents screen renderers contributed across every feature `ui` module.
 * @param sheetContents sheet renderers contributed across every sheet-owning feature `ui` module.
 * @param modifier layout modifier applied to the surface that hosts the home screen.
 */
@AppRootUi(presenter = RootPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun RootScreen(
    rootPresenter: RootPresenter,
    screenContents: Set<ScreenContent>,
    sheetContents: Set<SheetContent>,
    modifier: Modifier = Modifier,
) {
    val notificationPermissionState by rootPresenter.notificationPermissionState.collectAsStateWithLifecycle()
    val episodeSheetSlot by rootPresenter.episodeSheetSlot.collectAsStateWithLifecycle()
    val toastState by rootPresenter.toastState.collectAsStateWithLifecycle()
    val accountLimitBannerVisible by rootPresenter.accountLimitBannerVisible.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        rootPresenter.onNotificationPermissionResult(granted)
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

    RootContent(
        screenContents = screenContents,
        sheetContents = sheetContents,
        toastState = toastState,
        notificationPermissionState = notificationPermissionState,
        episodeSheetSlot = episodeSheetSlot,
        accountLimitBannerVisible = accountLimitBannerVisible,
        onRationaleAccepted = { rootPresenter.onRationaleAccepted() },
        onRationaleDismissed = { rootPresenter.onRationaleDismissed() },
        onDismissToast = { handleToastDismiss(rootPresenter, toastState) },
        onDismissAccountLimitBanner = { rootPresenter.onDismissAccountLimitBanner() },
        modifier = modifier,
    ) {
        HomeScreen(
            presenter = rootPresenter.homePresenter,
            modifier = Modifier.weight(1F),
        )
    }
}

@Composable
internal fun RootContent(
    screenContents: Set<ScreenContent>,
    sheetContents: Set<SheetContent>,
    toastState: ToastState,
    notificationPermissionState: NotificationPermissionState,
    episodeSheetSlot: ChildSlot<*, SheetChild>,
    accountLimitBannerVisible: Boolean,
    onRationaleAccepted: () -> Unit,
    onRationaleDismissed: () -> Unit,
    onDismissToast: () -> Unit,
    onDismissAccountLimitBanner: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val sheetChild = episodeSheetSlot.child?.instance
    val sheetRenderer = sheetChild?.let { child -> sheetContents.firstOrNull { it.matches(child) } }
    if (sheetChild != null && sheetRenderer != null) {
        sheetRenderer.content(sheetChild)
    }

    if (notificationPermissionState.showRationale) {
        ModalBottomSheet(
            onDismissRequest = onRationaleDismissed,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            NotificationRationaleContent(
                onEnable = onRationaleAccepted,
                onDismiss = onRationaleDismissed,
            )
        }
    }

    CompositionLocalProvider(LocalScreenContents provides screenContents) {
        Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
            ) {
                AccountLimitBanner(
                    onDismiss = onDismissAccountLimitBanner,
                    visible = accountLimitBannerVisible,
                )
                content()
            }

            TvManiacSnackBarHost(
                message = toastState.message,
                style = toastState.type.toSnackBarStyle(),
                persistent = toastState.persistent,
                loading = toastState.type == ToastType.Status,
                onDismiss = onDismissToast,
            )
        }
    }
}

private fun ToastType.toSnackBarStyle(): SnackBarStyle = when (this) {
    ToastType.Error -> SnackBarStyle.Error
    ToastType.Status -> SnackBarStyle.Info
}

private fun handleToastDismiss(rootPresenter: RootPresenter, state: ToastState) {
    when (state.type) {
        ToastType.Status -> rootPresenter.dismissSyncStatus()
        ToastType.Error -> state.id?.let(rootPresenter::onToastShown)
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RootScreenPreview(
    @PreviewParameter(RootPreviewParameterProvider::class) state: RootPreviewState,
) {
    RootContent(
        screenContents = emptySet(),
        sheetContents = emptySet(),
        toastState = state.toastState,
        notificationPermissionState = state.notificationPermissionState,
        episodeSheetSlot = ChildSlot<Nothing, Nothing>(),
        accountLimitBannerVisible = state.accountLimitBannerVisible,
        onRationaleAccepted = {},
        onRationaleDismissed = {},
        onDismissToast = {},
        onDismissAccountLimitBanner = {},
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}

internal data class RootPreviewState(
    val toastState: ToastState = ToastState(),
    val notificationPermissionState: NotificationPermissionState = NotificationPermissionState(),
    val accountLimitBannerVisible: Boolean = false,
)

internal class RootPreviewParameterProvider : PreviewParameterProvider<RootPreviewState> {
    override val values: Sequence<RootPreviewState>
        get() = sequenceOf(
            RootPreviewState(),
            RootPreviewState(
                notificationPermissionState = NotificationPermissionState(showRationale = true),
            ),
            RootPreviewState(
                toastState = ToastState(
                    message = "Connection lost",
                    type = ToastType.Error,
                ),
            ),
            RootPreviewState(accountLimitBannerVisible = true),
        )
}
