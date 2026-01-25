package com.thomaskioko.tvmaniac.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.android.feature.settings.R
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_api_disclaimer
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_github
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_section_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_source_code
import com.thomaskioko.tvmaniac.i18n.MR.strings.settings_about_version
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
internal fun AboutSheetContent(
    versionName: String,
    onGitHubClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_launcher),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TvManiac",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = settings_about_version.resolve(context).format(versionName),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Text(
                text = settings_about_section_title.resolve(context),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
            )

            Text(
                text = settings_about_description.resolve(context),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            AboutLinkRow(
                title = settings_about_source_code.resolve(context),
                linkText = settings_about_github.resolve(context),
                onClick = onGitHubClick,
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
        }

        Text(
            text = settings_about_api_disclaimer.resolve(context),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp),
        )
    }
}

@Composable
private fun AboutLinkRow(
    title: String,
    linkText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = linkText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@ThemePreviews
@Composable
private fun AboutSheetContentPreview() {
    TvManiacTheme {
        Surface {
            AboutSheetContent(
                versionName = "1.0.0",
                onGitHubClick = {},
            )
        }
    }
}
