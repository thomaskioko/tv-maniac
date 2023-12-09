package com.thomaskioko.tvmaniac.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.presentation.profile.DismissTraktDialog
import com.thomaskioko.tvmaniac.presentation.profile.ProfileActions
import com.thomaskioko.tvmaniac.presentation.profile.ProfilePresenter
import com.thomaskioko.tvmaniac.presentation.profile.ProfileState
import com.thomaskioko.tvmaniac.presentation.profile.ProfileStats
import com.thomaskioko.tvmaniac.presentation.profile.SettingsClicked
import com.thomaskioko.tvmaniac.presentation.profile.ShowTraktDialog
import com.thomaskioko.tvmaniac.presentation.profile.TraktLoginClicked
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@Composable
fun ProfileScreen(
    presenter: ProfilePresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.subscribeAsState()

    ProfileScreen(
        state = state,
        onAction = presenter::dispatch,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onAction: (ProfileActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = stringResource(id = R.string.menu_item_profile),
                onActionClicked = { onAction(SettingsClicked) },
                actionImageVector = Icons.Filled.Settings,
            )
        },
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background),
        content = { contentPadding ->

            if (state.userInfo != null) {
                UserProfile(
                    picUrl = state.userInfo?.userPicUrl,
                    userName = state.userInfo?.userName,
                    fullName = state.userInfo?.fullName,
                    profileStats = state.profileStats,
                    paddingValues = contentPadding,
                )
            } else {
                LoggedOutUi(
                    showTraktDialog = state.showTraktDialog,
                    paddingValues = contentPadding,
                    onAction = onAction,
                )
            }
        },
    )
}

@Composable
fun LoggedOutUi(
    showTraktDialog: Boolean,
    onAction: (ProfileActions) -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trakt_icon_red),
            tint = MaterialTheme.colorScheme.error,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 64.dp)
                .size(120.dp)
                .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Divider(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.trakt_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        val supportItems = listOf(
            stringResource(id = R.string.trakt_sync),
            stringResource(id = R.string.trakt_history),
            stringResource(id = R.string.trakt_release),
            stringResource(id = R.string.trakt_more),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(supportItems) { text ->
                TextListItem(text = text)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TvManiacTextButton(
            onClick = { onAction(ShowTraktDialog) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .align(Alignment.CenterHorizontally),
            buttonColors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onSecondary,
                containerColor = MaterialTheme.colorScheme.secondary,
            ),
            content = {
                Text(
                    text = stringResource(R.string.settings_title_connect_trakt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            },
        )

        TrackDialog(
            isVisible = showTraktDialog,
            onLoginClicked = { onAction(TraktLoginClicked) },
            onDismissDialog = { onAction(DismissTraktDialog) },
        )
    }
}

@Composable
fun TextListItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.secondary,
        )
        withStyle(tagStyle) {
            append("  •  ")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = divider,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Text(
            text = text,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun UserProfile(
    userName: String?,
    fullName: String?,
    picUrl: String?,
    profileStats: ProfileStats?,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp, top = 64.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        when {
            picUrl != null -> {
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp,
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    AsyncImageComposable(
                        model = picUrl,
                        contentDescription = stringResource(
                            R.string.cd_profile_pic,
                            fullName ?: userName ?: "",
                        ),
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally),

                    )
                }
            }

            else -> {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.cd_user_profile),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.trakt_user_name, fullName ?: userName ?: "Stranger"),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.size(24.dp))

        profileStats?.let {
            // Stats Row
            val lazyListState = rememberLazyListState()
            val contentPadding =
                PaddingValues(horizontal = Layout.bodyMargin, vertical = Layout.gutter)

            LazyRow(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = rememberSnapperFlingBehavior(
                    lazyListState = lazyListState,
                    snapOffsetForItem = SnapOffsets.Start,
                ),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                item { ShowTimeStats(profileStats = profileStats) }

                item { EpisodesStats(profileStats = profileStats) }
            }
        }
    }
}

@Composable
fun ShowTimeStats(
    profileStats: ProfileStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            Text(
                text = "Show Time",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )

            Row {
                DurationInfo(
                    value = profileStats.showMonths,
                    valueTitle = "Months",
                )

                DurationInfo(
                    value = profileStats.showDays,
                    valueTitle = "Days",
                )

                DurationInfo(
                    value = profileStats.showHours,
                    valueTitle = "Hours",
                )
            }
        }
    }
}

@Composable
fun EpisodesStats(
    profileStats: ProfileStats,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            Text(
                text = "Episodes Watched",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .align(Alignment.CenterHorizontally),
            )

            DurationInfo(
                value = profileStats.episodesWatched,
                valueTitle = "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
fun DurationInfo(
    value: String,
    modifier: Modifier = Modifier,
    valueTitle: String? = null,
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.size(4.dp))

        valueTitle?.let {
            Text(
                text = valueTitle,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
            )
        }
    }
}

@Composable
fun TrackDialog(
    isVisible: Boolean,
    onLoginClicked: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            initialAlpha = 0.4f,
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250),
        ),
    ) {
        BasicDialog(
            dialogTitle = stringResource(id = R.string.trakt_dialog_login_title),
            dialogMessage = stringResource(id = R.string.trakt_dialog_login_message),
            confirmButtonText = stringResource(id = R.string.login),
            onDismissDialog = onDismissDialog,
            confirmButtonClicked = onLoginClicked,
        )
    }
}

@ThemePreviews
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(ProfilePreviewParameterProvider::class)
    state: ProfileState,
) {
    TvManiacTheme {
        Surface {
            ProfileScreen(
                state = state,
                onAction = {},
            )
        }
    }
}
