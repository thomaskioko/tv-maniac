package com.thomaskioko.tvmaniac.ratingsheet.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thomaskioko.tvmaniac.compose.components.AsyncImageComposable
import com.thomaskioko.tvmaniac.compose.components.FilledHorizontalIconButton
import com.thomaskioko.tvmaniac.compose.components.PosterPlaceholder
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageType
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
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
        shape = sheetShape,
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
            .clip(sheetShape)
            .background(MaterialTheme.colorScheme.surface)
            .testTag(RatingSheetTestTags.SHEET_TEST_TAG),
    ) {
        SheetGrabber()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium)
                .padding(top = TvManiacSpacing.xSmall),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.large),
        ) {
            RatingTargetHeader(
                headerLabel = state.headerLabel,
                title = state.title,
                subtitle = state.subtitle,
                posterUrl = state.posterUrl,
                backdropUrl = state.backdropUrl,
            )

            Column(verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.small)) {
                Text(
                    text = state.scoreLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                ScoreGrid(
                    userRating = userRating,
                    onScoreSelected = { onAction(RatingSheetAction.RatingSelected(it)) },
                )
            }

            if (userRating != null) {
                FilledHorizontalIconButton(
                    text = state.removeRatingLabel,
                    onClick = { onAction(RatingSheetAction.RatingCleared) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .testTag(RatingSheetTestTags.CLEAR_RATING_BUTTON),
                    imageVector = Icons.Outlined.DeleteOutline,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.65f),
                )
            }
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.large))
    }
}

@Composable
private fun RatingTargetHeader(
    headerLabel: String,
    title: String,
    subtitle: String?,
    posterUrl: String?,
    backdropUrl: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        Text(
            text = headerLabel.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
            verticalAlignment = Alignment.Top,
        ) {
            when {
                backdropUrl != null -> RatingTargetImage(imageUrl = backdropUrl, imageType = ImageType.Backdrop)
                posterUrl != null -> RatingTargetImage(imageUrl = posterUrl, imageType = ImageType.Poster)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingTargetImage(
    imageUrl: String,
    imageType: ImageType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(HEADER_IMAGE_HEIGHT)
            .aspectRatio(imageType.aspect)
            .clip(MaterialTheme.shapes.medium),
    ) {
        PosterPlaceholder(
            modifier = Modifier.fillMaxSize(),
            imageSize = HEADER_IMAGE_PLACEHOLDER_ICON_SIZE,
        )
        AsyncImageComposable(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ScoreGrid(
    userRating: Int?,
    onScoreSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        (1..MAX_SCORE).chunked(SCORES_PER_ROW).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall)) {
                row.forEach { value ->
                    ScoreTile(
                        value = value,
                        selected = userRating == value,
                        onClick = { onScoreSelected(value) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreTile(
    value: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val performHaptic = rememberHapticFeedback()
    Box(
        modifier = modifier
            .height(SCORE_TILE_HEIGHT)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = UNSELECTED_TILE_BORDER_ALPHA),
                ),
                shape = MaterialTheme.shapes.medium,
            )
            .testTag(RatingSheetTestTags.score(value))
            .semantics {
                contentDescription = value.toString()
                this.selected = selected
            }
            .clickable {
                performHaptic()
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SheetGrabber(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.small),
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
        state = previewState(title = "Lioness", subtitle = "2023", posterUrl = "/lioness.jpg", userRating = null),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentRatedPreview() {
    RatingSheetContent(
        state = previewState(
            title = "Sacrificial Soldiers",
            subtitle = "Lioness • S1E1",
            backdropUrl = "/sacrificial-soldiers.jpg",
            userRating = 8,
        ),
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun RatingSheetContentSeasonRatedPreview() {
    RatingSheetContent(
        state = previewState(title = "Season 1", subtitle = "Lioness", posterUrl = "/season-1.jpg", userRating = 7),
        onAction = {},
    )
}

private fun previewState(
    title: String,
    subtitle: String?,
    userRating: Int?,
    posterUrl: String? = null,
    backdropUrl: String? = null,
) = RatingSheetState(
    headerLabel = "You're rating",
    title = title,
    subtitle = subtitle,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    scoreLabel = "Your rating",
    removeRatingLabel = "Remove rating",
    userRating = userRating,
)

private val sheetShape: CornerBasedShape
    @Composable get() = MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))

private const val MAX_SCORE = 10
private const val SCORES_PER_ROW = 5
private val SCORE_TILE_HEIGHT = 56.dp
private val HEADER_IMAGE_HEIGHT = 72.dp
private val HEADER_IMAGE_PLACEHOLDER_ICON_SIZE = 24.dp
private const val UNSELECTED_TILE_BORDER_ALPHA = 0.8f
