package com.thomaskioko.tvmaniac.app.ui

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.compose.components.BannerStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBanner
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.i18n.MR.strings.account_limit_banner_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.account_limit_dismiss_cta
import com.thomaskioko.tvmaniac.i18n.MR.strings.account_limit_upgrade_cta
import dev.icerock.moko.resources.compose.stringResource

private const val TRAKT_VIP_URL = "https://trakt.tv/vip"

@Composable
internal fun AccountLimitBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
) {
    val context = LocalContext.current

    TvManiacBanner(
        message = stringResource(account_limit_banner_message),
        onDismiss = onDismiss,
        modifier = modifier,
        visible = visible,
        style = BannerStyle.Error,
        dismissContentDescription = stringResource(account_limit_dismiss_cta),
        action = {
            Button(
                onClick = { openInCustomTab(context, TRAKT_VIP_URL) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocalContentColor.current,
                    contentColor = MaterialTheme.colorScheme.errorContainer,
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    text = stringResource(account_limit_upgrade_cta),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
    )
}

private fun openInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, url.toUri())
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AccountLimitBannerPreview() {
    AccountLimitBanner(onDismiss = {})
}
