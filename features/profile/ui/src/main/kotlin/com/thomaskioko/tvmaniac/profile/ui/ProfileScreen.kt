package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.AvatarComponent
import com.thomaskioko.tvmaniac.compose.components.OutlinedVerticalIconButton
import com.thomaskioko.tvmaniac.compose.components.PosterCard
import com.thomaskioko.tvmaniac.compose.components.RefreshCollapsableTopAppBar
import com.thomaskioko.tvmaniac.compose.components.ScrimButton
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomSheetScaffold
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.extensions.copy
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.LoginClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.MessageShown
import com.thomaskioko.tvmaniac.profile.presenter.ProfileAction.SettingsClicked
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileInfo
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileLabels
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileListItem
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileState
import com.thomaskioko.tvmaniac.profile.presenter.model.SectionState
import com.thomaskioko.tvmaniac.profile.ui.components.ProfileLoadingSkeleton
import com.thomaskioko.tvmaniac.profile.ui.components.StatsCard
import com.thomaskioko.tvmaniac.profile.ui.components.UnauthenticatedContent
import com.thomaskioko.tvmaniac.profile.ui.components.UserListsSection
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
                    labels = state.labels,
                    listCount = listCount,
                    userLists = state.userLists,
                    onLoginClicked = { onAction(LoginClicked) },
                    onViewLists = { onAction(ProfileAction.ViewListsClicked) },
                    onListClick = {},
                    onRetry = { onAction(ProfileAction.RefreshProfile) },
                    listState = listState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize(),
                )

                RefreshCollapsableTopAppBar(
                    listState = listState,
                    title = {
                        Text(
                            text = state.labels.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    actions = { showAppBarBackground ->
                        if (state.isLoading) {
                            ScrimButton(
                                show = showAppBarBackground,
                                onClick = {},
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                        }

                        ScrimButton(
                            show = showAppBarBackground,
                            onClick = { onAction(SettingsClicked) },
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag(ProfileTestTags.SETTINGS_BUTTON_TEST_TAG),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = state.labels.settingsContentDescription,
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
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
    labels: ProfileLabels,
    listCount: Int,
    userLists: SectionState<ProfileListItem>,
    onLoginClicked: () -> Unit,
    onViewLists: () -> Unit,
    onListClick: (Long) -> Unit,
    onRetry: () -> Unit,
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
                        editButtonLabel = labels.editButton,
                        avatarContentDescription = labels.profilePictureContentDescription,
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
                        labels = labels,
                        listCount = listCount,
                        onViewLists = onViewLists,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    UserListsSection(
                        userLists = userLists,
                        title = labels.userListsTitle,
                        viewAllLabel = labels.viewAllButton,
                        retryLabel = labels.retry,
                        onViewAll = onViewLists,
                        onListClick = onListClick,
                        onRetry = onRetry,
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        else -> {
            UnauthenticatedContent(
                labels = labels,
                onLoginClicked = onLoginClicked,
                modifier = modifier,
                contentPadding = contentPadding,
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
    editButtonLabel: String,
    avatarContentDescription: String,
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
                contentDescription = avatarContentDescription,
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
                            text = editButtonLabel,
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
            labels = sampleProfileLabels,
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
