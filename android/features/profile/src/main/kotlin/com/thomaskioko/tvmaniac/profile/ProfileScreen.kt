package com.thomaskioko.tvmaniac.profile

import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.BasicDialog
import com.thomaskioko.tvmaniac.compose.components.ColumnSpacer
import com.thomaskioko.tvmaniac.compose.components.Layout
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.util.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.resources.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    settingsClicked: () -> Unit
) {

    val scaffoldState = rememberScaffoldState()
    val profileState by viewModel.observeState().collectAsStateWithLifecycle()

    val loginLauncher = rememberLauncherForActivityResult(
        viewModel.buildLoginActivityResult()
    ) { result ->
        if (result != null) {
            viewModel.onLoginResult(result)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TvManiacTopBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.menu_item_profile),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(
                        onClick = settingsClicked,
                        modifier = Modifier.iconButtonBackgroundScrim()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null,
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
        },
        modifier = Modifier
            .background(color = MaterialTheme.colors.background)
            .statusBarsPadding(),
        content = { contentPadding ->
            ProfileScreenContent(
                contentPadding = contentPadding,
                profileState = profileState,
                onLoginClicked = {
                    loginLauncher.launch(Unit)
                    viewModel.dispatch(ProfileActions.DismissTraktDialog)
                },
                onConnectClicked = { viewModel.dispatch(ProfileActions.ShowTraktDialog) },
                onDismissDialogClicked = { viewModel.dispatch(ProfileActions.DismissTraktDialog) },
            )
        }
    )
}

@Composable
fun ProfileScreenContent(
    contentPadding: PaddingValues,
    profileState: ProfileStateContent,
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
    state: ProfileStateContent,
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
            tint = MaterialTheme.colors.error,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 64.dp)
                .size(120.dp)
                .align(Alignment.CenterHorizontally)
        )

        ColumnSpacer(value = 16)

        Divider(
            color = MaterialTheme.colors.secondary.copy(alpha = 0.8f)
        )

        ColumnSpacer(value = 16)

        Text(
            text = stringResource(id = R.string.trakt_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground,
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

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            TextButton(
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colors.onBackground,
                    backgroundColor = MaterialTheme.colors.secondary
                ),
                onClick = onConnectClicked,
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.settings_title_connect_trakt),
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
fun TextListItem(text: String) {

    val divider = buildAnnotatedString {
        val tagStyle = MaterialTheme.typography.overline.toSpanStyle().copy(
            color = MaterialTheme.colors.secondary
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
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.onBackground,
        )

        Text(
            text = text,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.fillMaxWidth(),
        )

    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun UserProfile(
    state: ProfileStateContent,
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
                            state.traktUser.fullName ?: state.traktUser.userName
                        ),
                        modifier = Modifier
                            .padding(top = 64.dp)
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colors.secondary, CircleShape)
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
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground,
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
    profileStats: ProfileStateContent.ProfileStats
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
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
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
    profileStats: ProfileStateContent.ProfileStats
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
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground,
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
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.size(4.dp))

        valueTitle?.let {
            Text(
                text = valueTitle,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.caption,
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

@Preview("Profile")
@Preview("Profile dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoggedInProfileScreenPreview() {
    TvManiacTheme {
        ProfileScreenContent(
            contentPadding = PaddingValues(0.dp),
            profileState = ProfileStateContent(
                loggedIn = true,
                showTraktDialog = false,
                traktUser = ProfileStateContent.TraktUser(
                    fullName = "Code Wizard",
                    userName = "@code_wizard",
                    userPicUrl = "",
                ),
                profileStats = ProfileStateContent.ProfileStats(
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

@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoggedOutProfileScreenPreview() {
    TvManiacTheme {
        ProfileScreenContent(
            contentPadding = PaddingValues(0.dp),
            profileState = ProfileStateContent(
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

