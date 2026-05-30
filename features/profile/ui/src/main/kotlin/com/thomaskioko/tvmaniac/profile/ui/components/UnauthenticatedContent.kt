package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileLabels
import com.thomaskioko.tvmaniac.profile.ui.sampleProfileLabels
import com.thomaskioko.tvmaniac.testtags.profile.ProfileTestTags

@Composable
internal fun UnauthenticatedContent(
    labels: ProfileLabels,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding.calculateTopPadding())
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 54.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = labels.unauthenticatedTitle,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                lineHeight = MaterialTheme.typography.displaySmall.fontSize.times(1.2f),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FeatureItem(
                    icon = Icons.Outlined.Search,
                    title = labels.featureDiscoverTitle,
                    description = labels.featureDiscoverDescription,
                )

                FeatureItem(
                    icon = Icons.Outlined.Tv,
                    title = labels.featureTrackTitle,
                    description = labels.featureTrackDescription,
                )

                FeatureItem(
                    icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                    title = labels.featureManageTitle,
                    description = labels.featureManageDescription,
                )

                FeatureItem(
                    icon = Icons.Outlined.AutoAwesome,
                    title = labels.featureMoreTitle,
                    description = labels.featureMoreDescription,
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
        ) {
            FilledTextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ProfileTestTags.SIGN_IN_BUTTON_TEST_TAG),
                onClick = onLoginClicked,
                shape = ButtonDefaults.shape,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
                content = {
                    Text(text = labels.signInButton)
                },
            )

            Text(
                text = labels.footerDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodySmall.fontSize.times(1.5f),
            )
        }
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(44.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.fontSize.times(1.3f),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun UnauthenticatedContentPreview() {
    UnauthenticatedContent(
        labels = sampleProfileLabels,
        onLoginClicked = {},
        contentPadding = PaddingValues(),
    )
}
