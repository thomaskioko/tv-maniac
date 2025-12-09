package com.thomaskioko.tvmaniac.settings.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel

@Composable
internal fun ThemeSelectorSection(
    selectedTheme: ThemeModel,
    onThemeSelected: (ThemeModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(300.dp),
    ) {
        items(
            items = ThemeModel.sortedByDisplayOrder,
            key = { it.name },
        ) { theme ->
            ThemePreviewSwatch(
                theme = theme,
                displayName = theme.getDisplayName(context),
                isSelected = theme == selectedTheme,
                onClick = { onThemeSelected(theme) },
            )
        }
    }
}

@Composable
private fun ThemeModel.getDisplayName(context: Context): String {
    return displayNameKey.resourceId.resolve(context)
}
