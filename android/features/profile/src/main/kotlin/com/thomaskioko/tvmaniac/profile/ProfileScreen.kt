package com.thomaskioko.tvmaniac.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.SnackBarErrorRetry
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTextButton
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.extensions.Layout
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    settingsClicked: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val profileState by viewModel.state.collectAsStateWithLifecycle()

    val loginLauncher = rememberLauncherForActivityResult(
        viewModel.buildLoginActivityResult()
    ) { result ->
        if (result != null) {
            viewModel.onLoginResult(result)
        }
    }

    Scaffold(
        topBar = {
            TvManiacTopBar(
                title = stringResource(id = R.string.menu_item_profile),
                onActionClicked = settingsClicked,
                actionImageVector = Icons.Filled.Settings,
            )
        },
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding(),
        content = { contentPadding ->

            when (profileState) {
                is ProfileError -> {
                    SnackBarErrorRetry(
                        snackBarHostState = snackbarHostState,
                        errorMessage = (profileState as ProfileError).error,
                        actionLabel = "Retry"
                    )
                }

                is ProfileStatsError -> {
                    SnackBarErrorRetry(
                        snackBarHostState = snackbarHostState,
                        errorMessage = (profileState as ProfileStatsError).error,
                        actionLabel = "Retry"
                    )
                }

                is ProfileContent -> {
                    ProfileScreenContent(
                        contentPadding = contentPadding,
                        profileState = profileState as ProfileContent,
                        onLoginClicked = {
                            loginLauncher.launch(Unit)
                            viewModel.dispatch(DismissTraktDialog)
                        },
                        onConnectClicked = { loginLauncher.launch(Unit) },
                        onDismissDialogClicked = { viewModel.dispatch(DismissTraktDialog) },
                    )
                }

            }
        }
    )
}

@Composable
fun ProfileScreenContent(
    contentPadding: PaddingValues,
    profileState: ProfileContent,
    onLoginClicked: () -> Unit,
    onConnectClicked: () -> Unit,
    onDismissDialogClicked: () -> Unit,
) {
    when {
        profileState.loggedIn -> UserProfile(
            state = profileState,
        )

        else ->
            TraktInfoContent(
                state = profileState,
                onLoginClicked = onLoginClicked,
                onConnectClicked = onConnectClicked,
                onDismissDialogClicked = onDismissDialogClicked,
            )
    }
}

@Composable
fun TraktInfoContent(
    state: ProfileContent,
    onConnectClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onDismissDialogClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
    ) {

        TrackDialog(
            isVisible = state.showTraktDialog,
            onLoginClicked = onLoginClicked,
            onDismissDialog = onDismissDialogClicked
        )

        Icon(
            painter = painterResource(id = R.drawable.trakt_icon_red),
            tint = MaterialTheme.colorScheme.error,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 64.dp)
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )

        ColumnSpacer(value = 16)

        Divider(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
        )

        ColumnSpacer(value = 16)

        Text(
            text = stringResource(id = R.string.trakt_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        )

        ColumnSpacer(value = 8)

        val supportItems = listOf(
            stringResource(id = R.string.trakt_sync),
            stringResource(id = R.string.trakt_history),
            stringResource(id = R.string.trakt_release),
            stringResource(id = R.string.trakt_more),
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(supportItems) { text ->
                TextListItem(text = text)
            }
        }

        ColumnSpacer(value = 16)

        TvManiacTextButton(
            onClick = onConnectClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .align(Alignment.CenterHorizontally),
            buttonColors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onSecondary,
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            content = {
                Text(
                    text = stringResource(R.string.settings_title_connect_trakt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        )
    }
}

@Composable
fun TextListItem(text: String) {

    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.labelMedium.toSpanStyle().copy(
            color = MaterialTheme.colorScheme.secondary
        )
        withStyle(tagStyle) {
            append("  •  ")
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
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
    state: ProfileContent,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
    ) {

        IconButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            when {
                state.loggedIn && state.traktUser?.userPicUrl != null -> {
                    AsyncImageComposable(
                        model = state.traktUser.userPicUrl,
                        contentDescription = stringResource(
                            R.string.cd_profile_pic,
                            state.traktUser.fullName ?: state.traktUser.userName ?: ""
                        ),
                        modifier = Modifier
                            .padding(top = 64.dp)
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .align(Alignment.CenterHorizontally)

                    )
                }

                else -> {
                    Icon(
                        imageVector = when {
                            state.loggedIn -> Icons.Default.Person
                            else -> Icons.Outlined.Person
                        },
                        contentDescription = stringResource(R.string.cd_user_profile)
                    )
                }
            }
        }

        ColumnSpacer(value = 16)

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(
                    R.string.trakt_user_name,
                    state.traktUser?.fullName ?: state.traktUser?.userName ?: "Stranger"
                ),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        state.profileStats?.let {
            //Stats Row
            val lazyListState = rememberLazyListState()
            val contentPadding =
                PaddingValues(horizontal = Layout.bodyMargin, vertical = Layout.gutter)

            LazyRow(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                flingBehavior = rememberSnapperFlingBehavior(
                    lazyListState = lazyListState,
                    snapOffsetForItem = SnapOffsets.Start
                ),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item { ShowTimeStats(profileStats = state.profileStats) }

                item { EpisodesStats(profileStats = state.profileStats) }
            }
        }
    }

}

@Composable
fun ShowTimeStats(
    profileStats: ProfileStats
) {

    Card(
        shape = MaterialTheme.shapes.medium,
    ) {

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = "Show Time",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            Row {

                DurationInfo(
                    value = profileStats.showMonths,
                    valueTitle = "Months"
                )

                DurationInfo(
                    value = profileStats.showDays,
                    valueTitle = "Days"
                )

                DurationInfo(
                    value = profileStats.showHours,
                    valueTitle = "Hours"
                )

            }
        }
    }
}

@Composable
fun EpisodesStats(
    profileStats: ProfileStats
) {
    Card(
        shape = MaterialTheme.shapes.medium,
    ) {

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = "Episodes Watched",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            DurationInfo(
                value = profileStats.episodesWatched,
                valueTitle = "",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

        }
    }
}

@Composable
fun DurationInfo(
    modifier: Modifier = Modifier,
    valueTitle: String? = null,
    value: String
) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = value,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.size(4.dp))

        valueTitle?.let {
            Text(
                text = valueTitle,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
            initialAlpha = 0.4f
        ),
        exit = fadeOut(
            // Overwrites the default animation with tween
            animationSpec = tween(durationMillis = 250)
        )
    ) {

        BasicDialog(
            dialogTitle = stringResource(id = R.string.settings_title_trakt_app),
            dialogMessage = stringResource(id = R.string.trakt_description),
            confirmButtonText = stringResource(id = R.string.login),
            onDismissDialog = onDismissDialog,
            confirmButtonClicked = onLoginClicked,
        )
    }
}

@ThemePreviews
@Composable
fun LoggedInContentPreview() {
    TvManiacTheme {
        Surface {
            ProfileScreenContent(
                contentPadding = PaddingValues(0.dp),
                profileState = ProfileContent(
                    loggedIn = true,
                    showTraktDialog = false,
                    traktUser = TraktUser(
                        fullName = "Code Wizard",
                        userName = "@code_wizard",
                        userPicUrl = "",
                        slug = "me"
                    ),
                    profileStats = ProfileStats(
                        collectedShows = "2000",
                        showMonths = "08",
                        showDays = "120",
                        showHours = "120",
                        episodesWatched = "8.1k"
                    )
                ),
                onLoginClicked = {},
                onDismissDialogClicked = {},
                onConnectClicked = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun LoggedOutContentPreview() {
    TvManiacTheme {
        Surface {
            ProfileScreenContent(
                contentPadding = PaddingValues(0.dp),
                profileState = ProfileContent(
                    loggedIn = false,
                    showTraktDialog = false,
                    traktUser = null,
                    profileStats = null
                ),
                onLoginClicked = {},
                onDismissDialogClicked = {},
                onConnectClicked = {},
            )
        }
    }
}

