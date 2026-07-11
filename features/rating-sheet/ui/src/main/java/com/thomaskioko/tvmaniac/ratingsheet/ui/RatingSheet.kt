package com.thomaskioko.tvmaniac.ratingsheet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.FilledHorizontalIconButton
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.util.rememberHapticFeedback
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetAction
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetPresenter
import com.thomaskioko.tvmaniac.ratingsheet.presenter.RatingSheetState
import com.thomaskioko.tvmaniac.testtags.ratingsheet.RatingSheetTestTags
import io.github.thomaskioko.codegen.annotations.SheetUi

@SheetUi(presenter = RatingSheetPresenter::class, parentScope = ActivityScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun RatingSheet(
    presenter: RatingSheetPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { presenter.dispatch(RatingSheetAction.Dismissed) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = SHEET_CORNER_RADIUS, topEnd = SHEET_CORNER_RADIUS),
        dragHandle = null,
        modifier = modifier,
    ) {
        RatingSheetContent(
            state = state,
            onAction = presenter::dispatch,
        )
    }
}

@Composable
internal fun RatingSheetContent(
    state: RatingSheetState,
    onAction: (RatingSheetAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val userRating = state.userRating
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = SHEET_CORNER_RADIUS, topEnd = SHEET_CORNER_RADIUS))
            .background(MaterialTheme.colorScheme.surface)
            .testTag(RatingSheetTestTags.SHEET_TEST_TAG),
    ) {
        SheetGrabber()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (star in 1..STAR_COUNT) {
                    val value = star * POINTS_PER_STAR
                    RatingStar(
                        value = value,
                        userRating = userRating,
                        onClick = { onAction(RatingSheetAction.RatingSelected(value)) },
                    )
                }
            }

            if (userRating != null) {
                FilledHorizontalIconButton(
                    text = state.removeRatingLabel,
                    onClick = { onAction(RatingSheetAction.RatingCleared) },
                    modifier = Modifier.testTag(RatingSheetTestTags.CLEAR_RATING_BUTTON),
                    imageVector = Icons.Outlined.DeleteOutline,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.65f),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RatingStar(
    value: Int,
    userRating: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val filled = userRating != null && userRating >= value
    val half = userRating != null && userRating == value - 1
    val performHaptic = rememberHapticFeedback()
    Box(
        modifier = modifier
            .size(40.dp)
            .testTag(RatingSheetTestTags.starRating(value))
            .clickable {
                performHaptic()
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = when {
                filled -> Icons.Filled.Star
                half -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            },
            contentDescription = value.toString(),
            modifier = Modifier.size(32.dp),
            tint = if (filled || half) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SheetGrabber(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(percent = 50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentUnratedPreview() {
    RatingSheetContent(
        state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = null),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentRatedPreview() {
    RatingSheetContent(
        state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = 8),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentHalfRatedPreview() {
    RatingSheetContent(
        state = RatingSheetState(title = "Your rating", removeRatingLabel = "Remove rating", userRating = 7),
        onAction = {},
    )
}

private const val STAR_COUNT = 5
private const val POINTS_PER_STAR = 2
private val SHEET_CORNER_RADIUS = 16.dp
