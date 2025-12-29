package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Calculates the scroll offset needed to make the next item in a lazy list partially visible.
 *
 * This function is useful for creating a "peek" effect where the next or previous item is
 * slightly visible to the user, indicating that there is more content to scroll to.
 *
 * @param itemWidth The width of a single item in the lazy list.
 * @param itemSpacing The spacing between items in the lazy list. Defaults to 0.dp.
 * @param visibleFraction The fraction of the next item that should be visible.
 *   A value of 0.1f means 10% of the next item will be visible. Defaults to 0.1f.
 * @return The calculated scroll offset in pixels. This value can be used with `LazyListState.scrollToItem()`
 *   to position the list correctly.
 */
@Composable
public fun calculateScrollOffset(
    itemWidth: Dp,
    itemSpacing: Dp = 0.dp,
    visibleFraction: Float = 0.1f,
): Int {
    val density = LocalDensity.current
    val totalItemWidth = with(density) { (itemWidth + itemSpacing).roundToPx() }
    return (totalItemWidth * (1f - visibleFraction)).toInt()
}
