package com.thomaskioko.tvmaniac.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.settings.presenter.BackClicked
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsMessageShown
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.components.AppearancePage
import com.thomaskioko.tvmaniac.settings.ui.components.BehaviorPage
import com.thomaskioko.tvmaniac.settings.ui.components.InfoPage
import com.thomaskioko.tvmaniac.settings.ui.components.LicensesPage
import com.thomaskioko.tvmaniac.settings.ui.components.NotificationsPage
import com.thomaskioko.tvmaniac.settings.ui.components.PrivacyPage
import com.thomaskioko.tvmaniac.settings.ui.components.SettingsRootContent
import com.thomaskioko.tvmaniac.settings.ui.components.TraktPage
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
                        SettingsPage.APPEARANCE -> AppearancePage(
                            state = state,
                            onAction = onAction,
                        )
                        SettingsPage.BEHAVIOR -> BehaviorPage(state = state, onAction = onAction)
                        SettingsPage.NOTIFICATIONS -> NotificationsPage(
                            state = state,
                            onAction = onAction,
                        )
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

private const val SETTINGS_PAGE_ANIMATION_MILLIS = 300

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
