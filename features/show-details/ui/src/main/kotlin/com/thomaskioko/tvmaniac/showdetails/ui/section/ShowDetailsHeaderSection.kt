package com.thomaskioko.tvmaniac.showdetails.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AutoAwesomeMotion
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ExpandingText
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.FilledVerticalIconButton
import com.thomaskioko.tvmaniac.compose.components.KenBurnsViewImage
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.extensions.backgroundGradient
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.following
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_action_rate
import com.thomaskioko.tvmaniac.i18n.MR.strings.unfollow
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsFollowClicked
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderAction
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsHeaderState
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowDetailsOpenShowList
import com.thomaskioko.tvmaniac.presenter.showdetails.header.ShowRatingClicked
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderState
import com.thomaskioko.tvmaniac.showdetails.ui.previewHeaderStateRated
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun ShowDetailsHeaderSection(presenter: ShowDetailsHeaderPresenter) {
    val state by presenter.state.collectAsState()
    ShowDetailsHeaderSection(state = state, onAction = presenter::dispatch)
}

@Composable
internal fun ShowDetailsHeaderSection(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
) {
    HeaderContent(state = state, onAction = onAction)
}

@Composable
private fun HeaderContent(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
) {
    val density = LocalDensity.current
    val containerHeight = with(density) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    val headerHeight = containerHeight / 1.5f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight),
    ) {
        KenBurnsViewImage(
            imageUrl = state.backdropImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
        )

        ShowBody(state = state, onAction = onAction)
    }
}

@Composable
private fun ShowBody(
    state: ShowDetailsHeaderState,
    onAction: (ShowDetailsHeaderAction) -> Unit,
) {
    val gradient = backgroundGradient()
    val surfaceGradient = remember(gradient) { gradient.reversed() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Brush.verticalGradient(surfaceGradient))
            .padding(horizontal = TvManiacSpacing.medium),
    ) {
        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = state.title,
                modifier = Modifier.testTag(ShowDetailsTestTags.SHOW_DETAILS_TITLE_TEST_TAG),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            ShowMetadata(
                releaseYear = state.year,
                status = state.status,
                language = state.language,
                communityRating = state.communityRating,
                communityVotes = state.communityVotes,
            )

            ExpandingText(
                text = state.overview,
                textStyle = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
            )

            Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

            GenreText(state.genres)

            Spacer(modifier = Modifier.height(TvManiacSpacing.xSmall))

            ShowDetailButtons(
                isFollowed = state.isInLibrary,
                canAddToList = state.canAddToList,
                isInList = state.isInList,
                listActionLabel = state.listActionLabel,
                userRating = state.userRating,
                onTrackShowClicked = { onAction(ShowDetailsFollowClicked(state.isInLibrary)) },
                onAddToList = { onAction(ShowDetailsOpenShowList) },
                onRateClicked = { onAction(ShowRatingClicked) },
            )
        }

        Spacer(Modifier.height(TvManiacSpacing.medium))
    }
}

@Composable
internal fun ShowMetadata(
    releaseYear: String,
    status: String?,
    language: String?,
    communityRating: Double?,
    communityVotes: Long?,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val divider = remember(colorScheme.secondary, typography.labelMedium) {
        buildAnnotatedString {
            val tagStyle = typography.labelMedium
                .toSpanStyle()
                .copy(color = colorScheme.secondary)
            withStyle(tagStyle) { append("  •  ") }
        }
    }

    val text = remember(
        status,
        releaseYear,
        language,
        colorScheme.secondary,
        colorScheme.onSurface,
        typography.labelMedium,
    ) {
        buildAnnotatedString {
            val statusStyle = typography.labelMedium
                .toSpanStyle()
                .copy(
                    color = colorScheme.secondary,
                    background = colorScheme.secondary.copy(alpha = 0.08f),
                )

            val tagStyle = typography.labelMedium
                .toSpanStyle()
                .copy(color = colorScheme.onSurface)

            if (!status.isNullOrBlank()) {
                withStyle(statusStyle) {
                    append(" ")
                    append(status)
                    append(" ")
                }
                append(divider)
            }

            withStyle(tagStyle) { append(releaseYear) }

            append(divider)
            language?.let { lang ->
                withStyle(tagStyle) { append(lang) }
                append(divider)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = TvManiacSpacing.xSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium,
                )

                if (communityRating != null) {
                    RatingBadge(
                        rating = formatCommunityRating(communityRating, communityVotes),
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = TvManiacSpacing.xxSmall),
                    )
                }
            }
        }
    }
}

private fun formatCommunityRating(rating: Double, votes: Long?): String {
    val ratingText = "%.1f".format(rating)
    return if (votes != null && votes > 0) "$ratingText ($votes)" else ratingText
}

@Composable
private fun RatingBadge(
    rating: String,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = tint,
        )
        Text(
            text = rating,
            style = MaterialTheme.typography.bodyMedium,
            color = tint,
            modifier = Modifier.padding(start = TvManiacSpacing.xxxSmall),
        )
    }
}

@Composable
private fun GenreText(
    genreList: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
    ) {
        items(
            items = genreList,
            key = { it },
            contentType = { "GenreItem" },
        ) { genre ->
            FilledTextButton(
                onClick = {},
                shape = MaterialTheme.shapes.small,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f),
                ),
                content = {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
            )
        }
    }
}

@Composable
internal fun ShowDetailButtons(
    isFollowed: Boolean,
    canAddToList: Boolean,
    isInList: Boolean,
    listActionLabel: String,
    userRating: Int?,
    onTrackShowClicked: (Boolean) -> Unit,
    onAddToList: () -> Unit,
    onRateClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = TvManiacSpacing.xSmall),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
    ) {
        val context = LocalContext.current
        FilledVerticalIconButton(
            modifier = Modifier
                .weight(1f)
                .testTag(
                    if (isFollowed) {
                        ShowDetailsTestTags.STOP_TRACKING_BUTTON_TEST_TAG
                    } else {
                        ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG
                    },
                ),
            shape = MaterialTheme.shapes.medium,
            text = if (isFollowed) unfollow.resolve(context) else following.resolve(context),
            imageVector = if (isFollowed) Icons.Filled.RemoveCircle else Icons.Filled.AddCircle,
            containerColor = if (isFollowed) {
                MaterialTheme.colorScheme.error.copy(alpha = 0.65f)
            } else {
                MaterialTheme.colorScheme.secondary
            },
            style = MaterialTheme.typography.labelMedium,
            onClick = { onTrackShowClicked(isFollowed) },
        )

        FilledVerticalIconButton(
            modifier = Modifier
                .weight(1f)
                .testTag(ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG),
            shape = MaterialTheme.shapes.medium,
            text = listActionLabel,
            imageVector = if (isInList) Icons.Filled.LibraryAddCheck else Icons.Outlined.AutoAwesomeMotion,
            containerColor = if (isInList) TvManiacTheme.colorScheme.success else MaterialTheme.colorScheme.secondary,
            contentColor = if (isInList) TvManiacTheme.colorScheme.onSuccess else MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.labelMedium,
            enabled = canAddToList,
            onClick = onAddToList,
        )

        FilledVerticalIconButton(
            modifier = Modifier
                .weight(1f)
                .testTag(ShowDetailsTestTags.RATE_BUTTON_TEST_TAG),
            shape = MaterialTheme.shapes.medium,
            text = label_action_rate.resolve(context),
            imageVector = if (userRating != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
            containerColor = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelMedium,
            onClick = onRateClicked,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsHeaderSectionPreview() {
    ShowDetailsHeaderSection(
        state = previewHeaderState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowDetailsHeaderSectionRatedPreview() {
    ShowDetailsHeaderSection(
        state = previewHeaderStateRated,
        onAction = {},
    )
}
