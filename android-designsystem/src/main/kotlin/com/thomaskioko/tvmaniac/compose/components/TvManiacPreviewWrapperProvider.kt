package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

/**
 * A [PreviewWrapperProvider] that provides the [TvManiacTheme] for previews.
 */
public class TvManiacPreviewWrapperProvider : PreviewWrapperProvider {
    @Composable
    override fun Wrap(content: @Composable () -> Unit) {
        TvManiacTheme {
            TvManiacBackground {
                content()
            }
        }
    }
}
