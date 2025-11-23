package com.thomaskioko.tvmaniac.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.FilledTextButton
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_discover_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_discover_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_manage_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_manage_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_more_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_more_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_track_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_feature_track_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_footer_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_sign_in_button
import com.thomaskioko.tvmaniac.i18n.MR.strings.profile_unauthenticated_title
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
internal fun UnauthenticatedContent(
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
) {
    Column(
        modifier = modifier
            .padding(contentPadding.calculateTopPadding())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 54.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = profile_unauthenticated_title.resolve(LocalContext.current),
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
                title = profile_feature_discover_title.resolve(LocalContext.current),
                description = profile_feature_discover_description.resolve(LocalContext.current),
            )

            FeatureItem(
                icon = Icons.Outlined.Tv,
                title = profile_feature_track_title.resolve(LocalContext.current),
                description = profile_feature_track_description.resolve(LocalContext.current),
            )

            FeatureItem(
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                title = profile_feature_manage_title.resolve(LocalContext.current),
                description = profile_feature_manage_description.resolve(LocalContext.current),
            )

            FeatureItem(
                icon = Icons.Outlined.AutoAwesome,
                title = profile_feature_more_title.resolve(LocalContext.current),
                description = profile_feature_more_description.resolve(LocalContext.current),
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = profile_footer_description.resolve(LocalContext.current),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodySmall.fontSize.times(1.5f),
            )

            FilledTextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLoginClicked,
                shape = ButtonDefaults.shape,
                buttonColors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                content = {
                    Text(text = profile_sign_in_button.resolve(LocalContext.current))
                },
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
@Composable
private fun UnauthenticatedContentPreview() {
    TvManiacTheme {
        Surface {
            UnauthenticatedContent(
                onLoginClicked = {},
                contentPadding = PaddingValues(),
            )
        }
    }
}
