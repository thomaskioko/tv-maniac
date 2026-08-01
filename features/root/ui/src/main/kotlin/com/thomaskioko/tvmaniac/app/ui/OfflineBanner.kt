package com.thomaskioko.tvmaniac.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.BannerStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBanner
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_dismiss
import com.thomaskioko.tvmaniac.i18n.MR.strings.status_connected
import com.thomaskioko.tvmaniac.i18n.MR.strings.status_no_connection
import com.thomaskioko.tvmaniac.presenter.root.model.ConnectivityBannerState
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun OfflineBanner(
    state: ConnectivityBannerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var displayState by remember { mutableStateOf(state) }
    if (state != ConnectivityBannerState.Hidden) {
        displayState = state
    }
    val backOnline = displayState == ConnectivityBannerState.BackOnline

    TvManiacBanner(
        message = stringResource(if (backOnline) status_connected else status_no_connection),
        onDismiss = onDismiss,
        modifier = modifier,
        visible = state != ConnectivityBannerState.Hidden,
        style = if (backOnline) BannerStyle.Success else BannerStyle.Warning,
        dismissContentDescription = stringResource(cd_dismiss),
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun OfflineBannerPreview() {
    OfflineBanner(
        state = ConnectivityBannerState.Offline,
        onDismiss = {},
    )
}
