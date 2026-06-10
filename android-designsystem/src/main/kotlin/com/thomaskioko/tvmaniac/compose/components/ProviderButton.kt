package com.thomaskioko.tvmaniac.compose.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.android.designsystem.R

@Composable
public fun ProviderButton(
    text: String,
    @DrawableRes logo: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(percent = 50),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
    ) {
        Icon(
            painter = painterResource(id = logo),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ProviderButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProviderButton(text = "Continue with Trakt", logo = R.drawable.ic_trakt_mono, onClick = {})
        ProviderButton(text = "Continue with Simkl", logo = R.drawable.ic_simkl_mono, onClick = {})
    }
}
