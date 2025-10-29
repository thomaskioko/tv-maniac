package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
internal fun CircularIndicator(
    totalItems: Int,
    currentPage: Int,
    isUserScrolling: Boolean,
    modifier: Modifier = Modifier,
) {
    val isInPreview = LocalInspectionMode.current
    var indicatorProgress by remember { mutableFloatStateOf(if (isInPreview) 1f else 0f) }

    LaunchedEffect(currentPage, isUserScrolling) {
        if (isInPreview) {
            indicatorProgress = 1f
            return@LaunchedEffect
        }

        if (!isUserScrolling) {
            indicatorProgress = 0f
            val startTime = System.currentTimeMillis()
            while (indicatorProgress < 1f) {
                val elapsed = System.currentTimeMillis() - startTime
                indicatorProgress = (elapsed / 4500f).coerceAtMost(1f)
                delay(16)
            }
        } else {
            indicatorProgress = 0f
        }
    }

    val maxVisibleDots = 8

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (totalItems <= maxVisibleDots) {
            repeat(totalItems) { index ->
                if (currentPage == index) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .width(25.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.5f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(indicatorProgress)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSecondary),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.5f)),
                    )
                }
            }
        } else {
            repeat(maxVisibleDots) { dotIndex ->
                val actualIndex = calculateOptimizedActualIndex(
                    dotIndex = dotIndex,
                    currentIndex = currentPage,
                    totalCount = totalItems,
                    maxVisible = maxVisibleDots,
                )

                val isActive = actualIndex == currentPage
                val isEdgeIndicator = (dotIndex == 0 && currentPage > maxVisibleDots - 2) ||
                    (dotIndex == maxVisibleDots - 1 && currentPage < totalItems - 2)

                if (isEdgeIndicator) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                    )
                } else if (isActive) {
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .width(25.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Gray.copy(alpha = 0.5f)),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(indicatorProgress)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.onSecondary),
                        )
                    }
                } else {
                    // Regular indicator
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.5f)),
                    )
                }
            }
        }
    }
}

/**
 * Optimized calculation function for determining the actual index with better performance.
 * Uses more efficient logic to reduce computational overhead.
 */
private fun calculateOptimizedActualIndex(
    dotIndex: Int,
    currentIndex: Int,
    totalCount: Int,
    maxVisible: Int,
): Int {
    val halfVisible = maxVisible / 2

    return when {
        // Early phase: show sequential items from start
        currentIndex < halfVisible -> dotIndex

        // Late phase: show sequential items from end
        currentIndex >= totalCount - halfVisible -> {
            totalCount - maxVisible + dotIndex
        }

        // Middle phase: center the active dot
        else -> currentIndex - halfVisible + dotIndex + 1
    }.coerceIn(0, totalCount - 1)
}
