package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ShimmerBox
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.ImageDimens
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Composable
internal fun ProfileLoadingSkeleton(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(contentPadding),
    ) {
        ProfileHeaderSkeleton()

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        ProfileStatsSkeleton(modifier = Modifier.padding(horizontal = TvManiacSpacing.medium))
    }
}

@Composable
private fun ProfileHeaderSkeleton(
    modifier: Modifier = Modifier,
) {
    ShimmerBox(
        modifier = modifier
            .fillMaxWidth()
            .height(ImageDimens.HeroProfileHeight),
        shape = RectangleShape,
    )
}

@Composable
private fun ProfileStatsSkeleton(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ShimmerBox(
            modifier = Modifier
                .width(120.dp)
                .height(28.dp),
        )

        Spacer(modifier = Modifier.height(TvManiacSpacing.medium))

        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
            ) {
                StatTileSkeleton(modifier = Modifier.weight(1f))
                StatTileSkeleton(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(TvManiacSpacing.small))
        }
    }
}

@Composable
private fun StatTileSkeleton(
    modifier: Modifier = Modifier,
) {
    ShimmerBox(
        modifier = modifier.height(138.dp),
        shape = MaterialTheme.shapes.large,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProfileLoadingSkeletonPreview() {
    ProfileLoadingSkeleton(
        contentPadding = PaddingValues(),
    )
}
