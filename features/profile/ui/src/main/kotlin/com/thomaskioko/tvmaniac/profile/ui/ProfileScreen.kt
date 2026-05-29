package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AnimatedCountText
import com.thomaskioko.tvmaniac.compose.components.AvatarComponent
import com.thomaskioko.tvmaniac.compose.components.OutlinedVerticalIconButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_profile_pic
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_settings
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_edit_button
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_episodes_watched
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_lists
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_shows_watched
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_stats_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_time_days
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_time_hours
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_time_months
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_watch_time
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.LoginClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.MessageShown
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.SettingsClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags
import io.github.thomaskioko.codegen.annotations.TabUi

@TabUi(presenter = ProfilePresenter::class, parentScope = ActivityScope::class)
@Composable
public fun ProfileScreen(
    presenter: ProfilePresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    ProfileScreen(
        modifier = modifier,
        state = state,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val listCount = state.listCount

    TvManiacBottomSheetScaffold(
        modifier = modifier.testTag(ProfileTestTags.SCREEN_TEST_TAG),
        showBottomSheet = false,
        sheetContent = {},
        onDismissBottomSheet = {},
        content = { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                ProfileContent(
                    showLoading = state.showLoading,
                    userProfile = state.userProfile,
                    listCount = listCount,
                    onLoginClicked = { onAction(LoginClicked) },
                    listState = listState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize(),
                )

                RefreshCollapsableTopAppBar(
                    listState = listState,
                    title = {
                        Text(
                            text = profile_title.resolve(LocalContext.current),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actionIcon = {
                        Box(
                            modifier = Modifier
                                .testTag(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG)
                                .clickable(onClick = { onAction(SettingsClicked) }),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = cd_settings.resolve(LocalContext.current),
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    },
                    isRefreshing = state.isLoading,
                    onActionIconClicked = { onAction(SettingsClicked) },
                )

                TvManiacSnackBarHost(
                    message = state.errorMessage?.message,
                    style = SnackBarStyle.Error,
                    onDismiss = { state.errorMessage?.let { onAction(MessageShown(it.id)) } },
                )
            }
        },
    )
}

@Composable
private fun ProfileContent(
    showLoading: Boolean,
    userProfile: ProfileInfo?,
    listCount: Int,
    onLoginClicked: () -> Unit,
    listState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    when {
        showLoading -> {
            ProfileLoadingSkeleton(
                contentPadding = contentPadding,
                modifier = modifier,
            )
        }

        userProfile != null -> {
            LazyColumn(
                modifier = modifier.testTag(ProfileTestTags.userCard(userProfile.slug)),
                state = listState,
                contentPadding = contentPadding.copy(copyTop = false),
            ) {
                item {
                    HeaderContent(
                        scrollState = scrollState,
                        imageUrl = userProfile.backgroundUrl,
                        username = userProfile.fullName ?: userProfile.username,
                        avatarUrl = userProfile.avatarUrl,
                        listState = listState,
                        onEditClicked = {},
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    StatsCard(
                        stats = userProfile.stats,
                        listCount = listCount,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        else -> {
            UnauthenticatedContent(
                onLoginClicked = onLoginClicked,
                modifier = modifier,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun StatsCard(
    stats: ProfileStats,
    listCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = profile_stats_title.resolve(LocalContext.current),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(
                imageVector = Icons.Outlined.PlayCircle,
                title = profile_episodes_watched.resolve(LocalContext.current),
                modifier = Modifier.weight(1f),
            ) {
                AnimatedCountText(count = stats.episodesWatched)
            }

            StatTile(
                imageVector = Icons.Outlined.Tv,
                title = profile_shows_watched.resolve(LocalContext.current),
                modifier = Modifier.weight(1f),
            ) {
                AnimatedCountText(count = stats.showsWatched)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(
                imageVector = Icons.Outlined.Schedule,
                title = profile_watch_time.resolve(LocalContext.current),
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    MiniStat(
                        value = stats.months,
                        label = profile_time_months.resolve(LocalContext.current),
                    )
                    MiniStat(
                        value = stats.days,
                        label = profile_time_days.resolve(LocalContext.current),
                    )
                    MiniStat(
                        value = stats.hours,
                        label = profile_time_hours.resolve(LocalContext.current),
                    )
                }
            }

            StatTile(
                imageVector = Icons.AutoMirrored.Filled.List,
                title = profile_lists.resolve(LocalContext.current),
                modifier = Modifier.weight(1f),
            ) {
                AnimatedCountText(count = listCount)
            }
        }
    }
}

@Composable
private fun MiniStat(
    value: Int,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ProfileLoadingSkeleton(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(contentPadding),
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            shape = RectangleShape,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ShimmerBox(
                modifier = Modifier
                    .width(120.dp)
                    .height(28.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HeaderContent(
    scrollState: ScrollState,
    imageUrl: String?,
    username: String,
    avatarUrl: String?,
    listState: LazyListState,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val headerOffset by remember {
        derivedStateOf {
            IntOffset(
                x = 0,
                y = if (listState.firstVisibleItemIndex == 0) {
                    listState.firstVisibleItemScrollOffset / 2
                } else {
                    0
                },
            )
        }
    }

    val posterOffset by remember {
        derivedStateOf {
            IntOffset(0, scrollState.value / 2)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .clipToBounds()
            .offset { headerOffset },
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .offset { posterOffset },
        )

        val brush = remember {
            Brush.verticalGradient(
                listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.8f),
                ),
                startY = 100f,
                endY = 700f,
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush),
        )

        // Profile Content
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            AvatarComponent(
                imageUrl = avatarUrl,
                size = 80.dp,
                placeholderIcon = Icons.Filled.Person,
                contentDescription = stringResource(
                    cd_profile_pic.resourceId,
                    username,
                ),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .testTag(ProfileTestTags.USERNAME_TEST_TAG),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedVerticalIconButton(
                    onClick = onEditClicked,
                    shape = MaterialTheme.shapes.medium,
                    borderColor = MaterialTheme.colorScheme.onSecondary,
                    text = {
                        Text(
                            text = profile_edit_button.resolve(LocalContext.current),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    },
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProfileScreenLoadingPreview() {
    ProfileScreen(
        state = ProfileState(
            isLoading = true,
            userProfile = null,
            errorMessage = null,
            authenticated = false,
        ),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(ProfilePreviewParameterProvider::class) state: ProfileState,
) {
    ProfileScreen(
        state = state,
        onAction = {},
    )
}
