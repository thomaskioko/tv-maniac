package com.thomaskioko.tvmaniac.settings.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.thomaskioko.tvmaniac.android.feature.settings.R
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacAlertDialog
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.domain.theme.ImageQuality
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_trakt_dialog_button_secondary
import com.thomaskioko.tvmaniac.i18n.MR.strings.logout
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_message
import com.thomaskioko.tvmaniac.i18n.MR.strings.trakt_dialog_logout_title
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.BackgroundSyncToggled
import com.thomaskioko.tvmaniac.settings.presenter.CrashReportingToggled
import com.thomaskioko.tvmaniac.settings.presenter.DismissTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.EpisodeNotificationsToggled
import com.thomaskioko.tvmaniac.settings.presenter.ImageQualitySelected
import com.thomaskioko.tvmaniac.settings.presenter.IncludeSpecialsToggled
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsMessageShown
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.ShowTraktDialog
import com.thomaskioko.tvmaniac.settings.presenter.ThemeSelected
import com.thomaskioko.tvmaniac.settings.presenter.TraktLoginClicked
import com.thomaskioko.tvmaniac.settings.presenter.TraktLogoutClicked
import com.thomaskioko.tvmaniac.settings.presenter.VersionClicked
import com.thomaskioko.tvmaniac.settings.presenter.YoutubeToggled
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = SettingsPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun SettingsScreen(
    presenter: SettingsPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    SettingsScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )

    TvManiacSnackBarHost(
        message = state.message?.message,
        style = SnackBarStyle.Error,
        onDismiss = { state.message?.let { presenter.dispatch(SettingsMessageShown(it.id)) } },
    )
}

@Composable
internal fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state.currentPage != SettingsPage.ROOT) {
        onAction(BackClicked)
    }

    Scaffold(
        modifier = modifier.testTag(SettingsTestTags.SCREEN_TEST_TAG),
        topBar = {
            TvManiacTopBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .testTag(SettingsTestTags.BACK_BUTTON_TEST_TAG)
                            .clickable(onClick = { onAction(BackClicked) })
                            .padding(16.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = state.labels.back,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
                title = {
                    Text(
                        text = state.currentPageTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                    )
                },
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        content = { innerPadding ->
            if (state.isLoading) {
                SettingsLoadingUi(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                )
            } else {
                AnimatedContent(
                    targetState = state.currentPage,
                    transitionSpec = {
                        if (targetState != SettingsPage.ROOT) {
                            (slideInHorizontally(tween(SETTINGS_PAGE_ANIMATION_MILLIS)) { it } + fadeIn()) togetherWith
                                (slideOutHorizontally(tween(SETTINGS_PAGE_ANIMATION_MILLIS)) { -it / 4 } + fadeOut())
                        } else {
                            (slideInHorizontally(tween(SETTINGS_PAGE_ANIMATION_MILLIS)) { -it / 4 } + fadeIn()) togetherWith
                                (slideOutHorizontally(tween(SETTINGS_PAGE_ANIMATION_MILLIS)) { it } + fadeOut())
                        }
                    },
                    label = "settings_page",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) { page ->
                    when (page) {
                        SettingsPage.ROOT -> SettingsRootContent(state = state, onAction = onAction)
                        SettingsPage.APPEARANCE -> AppearancePage(state = state, onAction = onAction)
                        SettingsPage.BEHAVIOR -> BehaviorPage(state = state, onAction = onAction)
                        SettingsPage.NOTIFICATIONS -> NotificationsPage(state = state, onAction = onAction)
                        SettingsPage.PRIVACY -> PrivacyPage(state = state, onAction = onAction)
                        SettingsPage.INFO -> InfoPage(state = state, onAction = onAction)
                        SettingsPage.LICENSES -> LicensesPage(state = state)
                        SettingsPage.TRAKT -> TraktPage(state = state, onAction = onAction)
                    }
                }
            }
        },
    )
}

@Composable
private fun SettingsRootContent(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        state.rootGroups.forEach { group ->
            item { SettingsSectionLabel(text = group.label) }

            item {
                SettingsGroup {
                    group.items.forEachIndexed { index, categoryItem ->
                        SettingsNavigationRow(
                            modifier = Modifier.testTag(rootRowTestTag(categoryItem.page)),
                            icon = rootRowIcon(categoryItem.page),
                            title = categoryItem.title,
                            description = categoryItem.summary,
                            onClick = { onAction(OpenSettingsPage(categoryItem.page)) },
                        )
                        if (index != group.items.lastIndex) {
                            SettingsGroupDivider()
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        item {
            Text(
                text = state.labels.version,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

private fun rootRowTestTag(page: SettingsPage): String = when (page) {
    SettingsPage.APPEARANCE -> SettingsTestTags.GENERAL_APPEARANCE_ROW_TEST_TAG
    SettingsPage.BEHAVIOR -> SettingsTestTags.GENERAL_BEHAVIOR_ROW_TEST_TAG
    SettingsPage.NOTIFICATIONS -> SettingsTestTags.GENERAL_NOTIFICATIONS_ROW_TEST_TAG
    SettingsPage.PRIVACY -> SettingsTestTags.GENERAL_PRIVACY_ROW_TEST_TAG
    SettingsPage.INFO -> SettingsTestTags.ABOUT_INFO_ROW_TEST_TAG
    SettingsPage.LICENSES -> SettingsTestTags.ABOUT_LICENSES_ROW_TEST_TAG
    SettingsPage.TRAKT -> SettingsTestTags.ACCOUNT_TRAKT_ROW_TEST_TAG
    SettingsPage.ROOT -> ""
}

private fun rootRowIcon(page: SettingsPage): ImageVector = when (page) {
    SettingsPage.APPEARANCE -> Icons.Filled.Palette
    SettingsPage.BEHAVIOR -> Icons.Filled.Tune
    SettingsPage.NOTIFICATIONS -> Icons.Filled.Notifications
    SettingsPage.PRIVACY -> Icons.Filled.Security
    SettingsPage.INFO -> Icons.Filled.Info
    SettingsPage.LICENSES -> Icons.Filled.Description
    SettingsPage.TRAKT -> Icons.Filled.Person
    SettingsPage.ROOT -> Icons.Filled.Info
}

@Composable
private fun AppearancePage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsSectionLabel(text = state.labels.themeTitle) }

        item {
            SettingsGroup {
                ThemeSelectorSection(
                    selectedTheme = state.theme,
                    onThemeSelected = { onAction(ThemeSelected(it)) },
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item { SettingsSectionLabel(text = state.labels.imageQualityTitle) }

        item {
            SettingsGroup {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ImageQualityChip(
                            label = state.labels.imageQualityAuto,
                            quality = ImageQuality.AUTO,
                            isSelected = state.imageQuality == ImageQuality.AUTO,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.AUTO)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityHigh,
                            quality = ImageQuality.HIGH,
                            isSelected = state.imageQuality == ImageQuality.HIGH,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.HIGH)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityMedium,
                            quality = ImageQuality.MEDIUM,
                            isSelected = state.imageQuality == ImageQuality.MEDIUM,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.MEDIUM)) },
                        )
                        ImageQualityChip(
                            label = state.labels.imageQualityLow,
                            quality = ImageQuality.LOW,
                            isSelected = state.imageQuality == ImageQuality.LOW,
                            onClick = { onAction(ImageQualitySelected(ImageQuality.LOW)) },
                        )
                    }
                    Text(
                        text = state.labels.imageQualityDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun BehaviorPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val syncDescription = buildString {
        append(state.labels.syncDescription)
        state.labels.lastSync?.let {
            append("\n")
            append(it)
        }
    }

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchRow(
                    icon = Icons.Filled.Sync,
                    title = state.labels.syncTitle,
                    description = syncDescription,
                    checked = state.backgroundSyncEnabled,
                    onCheckedChange = { onAction(BackgroundSyncToggled(it)) },
                )
                SettingsGroupDivider()
                SettingsSwitchRow(
                    icon = Icons.Filled.VideoLibrary,
                    title = state.labels.includeSpecialsTitle,
                    description = state.labels.includeSpecialsDescription,
                    checked = state.includeSpecials,
                    onCheckedChange = { onAction(IncludeSpecialsToggled(it)) },
                )
                SettingsGroupDivider()
                SettingsSwitchRow(
                    icon = Icons.Filled.Tv,
                    title = state.labels.youtubeTitle,
                    description = state.labels.youtubeDescription,
                    checked = state.openTrailersInYoutube,
                    onCheckedChange = { onAction(YoutubeToggled(it)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun NotificationsPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.Notifications,
                    title = state.labels.episodeNotificationsTitle,
                    description = state.labels.episodeNotificationsDescription,
                    checked = state.episodeNotificationsEnabled,
                    onCheckedChange = { onAction(EpisodeNotificationsToggled(it)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun PrivacyPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchRow(
                    icon = Icons.Filled.BugReport,
                    title = state.labels.crashReportingTitle,
                    description = state.labels.crashReportingDescription,
                    checked = state.crashReportingEnabled,
                    onCheckedChange = { onAction(CrashReportingToggled(it)) },
                )
                SettingsGroupDivider()
                SettingsNavigationRow(
                    icon = Icons.Filled.Security,
                    title = state.labels.privacyPolicy,
                    onClick = { openInCustomTab(context, state.privacyPolicyUrl) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun InfoPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_app_launcher),
            contentDescription = null,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp)),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = state.labels.appName,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.labels.version,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable(onClick = { onAction(VersionClicked) }),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.labels.aboutDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsGroup {
            SettingsNavigationRow(
                icon = Icons.Filled.Code,
                title = state.labels.sourceCode,
                description = state.labels.github,
                onClick = { openInCustomTab(context, state.githubUrl) },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = state.labels.apiDisclaimer,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
        )
    }
}

@Composable
private fun LicensesPage(
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

@Composable
private fun TraktPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.trakt_logo),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = state.labels.traktTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.labels.traktDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item { SettingsSectionLabel(text = state.labels.traktAuthentication) }

        item {
            SettingsGroup {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = state.labels.traktConnected,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = state.labels.traktConnectedDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        modifier = Modifier.testTag(SettingsTestTags.TRAKT_ACCOUNT_ROW_TEST_TAG),
                        enabled = !state.isProcessingTraktAuth,
                        onClick = {
                            onAction(if (state.isAuthenticated) ShowTraktDialog else TraktLoginClicked)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                    ) {
                        if (state.isProcessingTraktAuth) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        } else {
                            Text(text = if (state.isAuthenticated) state.labels.logout else state.labels.login)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    LogoutDialog(
        isVisible = state.showTraktDialog,
        onLogoutClicked = { onAction(TraktLogoutClicked) },
        onDismissDialog = { onAction(DismissTraktDialog) },
    )
}

@Composable
private fun ImageQualityChip(
    label: String,
    quality: ImageQuality,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = Modifier.testTag(SettingsTestTags.imageQualityChip(quality.name)),
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onSecondary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.secondary,
            enabled = true,
            selected = isSelected,
        ),
        shape = RoundedCornerShape(20.dp),
    )
}

@Composable
private fun LogoutDialog(
    isVisible: Boolean,
    onLogoutClicked: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    val context = LocalContext.current

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(initialAlpha = 0.4f),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)),
    ) {
        TvManiacAlertDialog(
            title = trakt_dialog_logout_title.resolve(context),
            message = trakt_dialog_logout_message.resolve(context),
            confirmButtonText = logout.resolve(context),
            dismissButtonText = label_settings_trakt_dialog_button_secondary.resolve(context),
            onConfirm = onLogoutClicked,
            onDismiss = onDismissDialog,
            confirmButtonTestTag = SettingsTestTags.LOGOUT_DIALOG_CONFIRM_BUTTON_TEST_TAG,
            dismissButtonTestTag = SettingsTestTags.LOGOUT_DIALOG_DISMISS_BUTTON_TEST_TAG,
        )
    }
}

private fun openInCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, url.toUri())
}

private const val SETTINGS_PAGE_ANIMATION_MILLIS = 300
private const val TMDB_URL = "https://www.themoviedb.org"
private const val TRAKT_URL = "https://trakt.tv"

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsPreviewParameterProvider::class) state: SettingsState,
) {
    SettingsScreen(
        state = state,
        onAction = {},
    )
}
