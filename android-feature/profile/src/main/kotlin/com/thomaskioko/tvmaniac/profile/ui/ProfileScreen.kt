package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.OutlinedVerticalIconButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_profile_pic
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_settings
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_edit_button
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_episodes_watched
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

@Composable
public fun ProfileScreen(
    presenter: ProfilePresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    ProfileScreen(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = presenter::dispatch,
    )
}

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    snackbarHostState: SnackbarHostState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(
                message = error.message,
                duration = SnackbarDuration.Short,
            )
            onAction(MessageShown(error.id))
        }
    }

    TvManiacBottomSheetScaffold(
        modifier = modifier,
        showBottomSheet = false,
        sheetContent = {},
        onDismissBottomSheet = {},
        content = { contentPadding ->
            Box(Modifier.fillMaxSize()) {
                ProfileContent(
                    isLoading = state.isLoading,
                    userProfile = state.userProfile,
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
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = cd_settings.resolve(LocalContext.current),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    isRefreshing = state.isRefreshing,
                    onActionIconClicked = { onAction(SettingsClicked) },
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                )
            }
        },
    )
}

@Composable
private fun ProfileContent(
    isLoading: Boolean,
    userProfile: ProfileInfo?,
    onLoginClicked: () -> Unit,
    listState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    when {
        userProfile == null && !isLoading -> {
            UnauthenticatedContent(
                onLoginClicked = onLoginClicked,
                modifier = modifier,
                contentPadding = contentPadding,
            )
        }

        userProfile == null && isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        userProfile != null -> {
            LazyColumn(
                modifier = modifier,
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
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun StatsCard(
    stats: ProfileStats,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                StatsCardItem(
                    imageVector = Icons.Outlined.Tv,
                    title = profile_watch_time.resolve(LocalContext.current),
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            StatColumnItem(
                                value = stats.months.toString(),
                                title = profile_time_months.resolve(LocalContext.current),
                            )
                            StatColumnItem(
                                value = stats.days.toString(),
                                title = profile_time_days.resolve(LocalContext.current),
                            )
                            StatColumnItem(
                                value = stats.hours.toString(),
                                title = profile_time_hours.resolve(LocalContext.current),
                            )
                        }
                    },
                )
            }

            item {
                StatsCardItem(
                    imageVector = Icons.Outlined.Tv,
                    title = profile_episodes_watched.resolve(LocalContext.current),
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            StatColumnItem(
                                value = "%,d".format(stats.episodesWatched),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun StatColumnItem(
    value: String,
    title: String? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Normal,
        )
        title?.let {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) { offset.toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .clipToBounds()
            .offset {
                IntOffset(
                    x = 0,
                    y = if (listState.firstVisibleItemIndex == 0) {
                        listState.firstVisibleItemScrollOffset / 2
                    } else {
                        0
                    },
                )
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        PosterCard(
            imageUrl = imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = offsetDp),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f),
                        ),
                        startY = 100f,
                        endY = 700f,
                    ),
                ),
        )

        // Profile Content
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImageComposable(
                    model = avatarUrl,
                    contentDescription = stringResource(
                        cd_profile_pic.resourceId,
                        username,
                    ),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary,
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
@Composable
private fun ProfileScreenLoadingPreview() {
    TvManiacTheme {
        Surface {
            ProfileScreen(
                state = ProfileState(
                    isLoading = true,
                    isRefreshing = true,
                    userProfile = null,
                    errorMessage = null,
                    authenticated = false,
                ),
                snackbarHostState = SnackbarHostState(),
                onAction = {},
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(ProfilePreviewParameterProvider::class) state: ProfileState,
) {
    TvManiacTheme {
        Surface {
            ProfileScreen(
                state = state,
                snackbarHostState = SnackbarHostState(),
                onAction = {},
            )
        }
    }
}
