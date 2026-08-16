package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_action_rate
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_action_watch_again
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.header.MarkShowWatchedClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderAction
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderState
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsMoreDismissed
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowRatingClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.header.WatchAgainClicked
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@Composable
internal fun ShowDetailsMoreMenu(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    DropdownMenu(
        expanded = state.showMoreSheet,
        onDismissRequest = { onAction(ShowDetailsMoreDismissed) },
        modifier = modifier.testTag(ShowDetailsTestTags.MORE_MENU_TEST_TAG),
    ) {
        MoreMenuItem(
            label = label_action_rate.resolve(context),
            imageVector = if (state.userRating != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
            tag = ShowDetailsTestTags.RATE_BUTTON_TEST_TAG,
            onClick = { onAction(ShowRatingClicked) },
        )

        if (state.canMarkShowWatched) {
            MoreMenuItem(
                label = state.markShowWatchedLabel,
                imageVector = Icons.Filled.PlaylistAddCheck,
                tag = ShowDetailsTestTags.MARK_SHOW_WATCHED_BUTTON_TEST_TAG,
                onClick = { onAction(MarkShowWatchedClicked) },
            )
        }

        if (state.canWatchAgain) {
            MoreMenuItem(
                label = label_action_watch_again.resolve(context),
                imageVector = Icons.Filled.DoneAll,
                tag = ShowDetailsTestTags.WATCH_AGAIN_BUTTON_TEST_TAG,
                onClick = { onAction(WatchAgainClicked) },
            )
        }
    }
}

@Composable
private fun MoreMenuItem(
    label: String,
    imageVector: ImageVector,
    tag: String,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(text = label, style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(imageVector = imageVector, contentDescription = null) },
        onClick = onClick,
        modifier = Modifier.testTag(tag),
    )
}
