package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing

@Composable
public fun PaddingValues.copy(
    copyStart: Boolean = true,
    copyTop: Boolean = true,
    copyEnd: Boolean = true,
    copyBottom: Boolean = true,
): PaddingValues = PaddingValues(
    start = if (copyStart) calculateStartPadding(LayoutDirection.Ltr) else TvManiacSpacing.none,
    top = if (copyTop) calculateTopPadding() else TvManiacSpacing.none,
    end = if (copyEnd) calculateEndPadding(LayoutDirection.Ltr) else TvManiacSpacing.none,
    bottom = if (copyBottom) calculateBottomPadding() else TvManiacSpacing.none,
)
