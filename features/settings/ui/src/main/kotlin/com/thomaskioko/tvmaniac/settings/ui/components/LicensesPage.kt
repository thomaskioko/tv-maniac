package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.android.feature.settings.R
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.SettingsLinkRow
import com.thomaskioko.tvmaniac.settings.ui.SettingsSectionLabel
import com.thomaskioko.tvmaniac.settings.ui.licensesState
import com.thomaskioko.tvmaniac.settings.ui.openInCustomTab
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun LicensesPage(
    state: SettingsState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsSectionLabel(text = state.labels.licensesApp) }

        item {
            SettingsGroup {
                SettingsLinkRow(
                    title = state.labels.appName,
                    body = state.labels.aboutDescription,
                    link = state.githubUrl,
                    onClick = { openInCustomTab(context, state.githubUrl) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsSectionLabel(text = state.labels.licensesData) }

        item {
            SettingsGroup {
                SettingsLinkRow(
                    leadingIcon = painterResource(id = R.drawable.tmdb_logo),
                    title = state.labels.tmdbTitle,
                    body = state.labels.tmdbBody,
                    link = TMDB_URL,
                    onClick = { openInCustomTab(context, TMDB_URL) },
                )
                SettingsGroupDivider()
                SettingsLinkRow(
                    leadingIcon = painterResource(id = R.drawable.trakt_logo),
                    title = state.labels.traktTitle,
                    body = state.labels.traktBody,
                    link = TRAKT_URL,
                    onClick = { openInCustomTab(context, TRAKT_URL) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

private const val TMDB_URL = "https://www.themoviedb.org"
private const val TRAKT_URL = "https://trakt.tv"

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LicensesPagePreview() {
    LicensesPage(state = licensesState)
}
