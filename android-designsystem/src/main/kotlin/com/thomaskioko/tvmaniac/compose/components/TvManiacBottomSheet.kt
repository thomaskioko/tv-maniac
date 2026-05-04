package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun TvManiacBottomSheetScaffold(
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    onDismissBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    sheetPeekHeight: Dp = 0.dp,
    showBottomSheet: Boolean = false,
    skipHiddenState: Boolean = false,
    sheetShadowElevation: Dp = 0.dp,
    initialSheetState: SheetValue = SheetValue.Hidden,
    sheetShape: Shape = RoundedCornerShape(5.dp),
    containerColor: Color = MaterialTheme.colorScheme.background,
    sheetContainerColor: Color = MaterialTheme.colorScheme.background,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    sheetDragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
) {
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = initialSheetState,
        skipHiddenState = skipHiddenState,
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
    )

    var isSheetContentComposing by remember { mutableStateOf(initialSheetState != SheetValue.Hidden) }

    LaunchedEffect(key1 = showBottomSheet) {
        if (showBottomSheet) {
            isSheetContentComposing = true
            bottomSheetState.expand()
        } else {
            bottomSheetState.hide()
            isSheetContentComposing = false
        }
    }

    LaunchedEffect(bottomSheetScaffoldState.bottomSheetState.currentValue) {
        if (bottomSheetScaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
            onDismissBottomSheet()
        }
    }

    BottomSheetScaffold(
        modifier = modifier,
        topBar = topBar,
        sheetPeekHeight = sheetPeekHeight,
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = sheetShape,
        sheetShadowElevation = sheetShadowElevation,
        sheetContent = {
            if (isSheetContentComposing) {
                sheetContent()
            }
        },
        sheetContainerColor = sheetContainerColor,
        snackbarHost = snackbarHost,
        containerColor = containerColor,
        sheetDragHandle = {
            if (isSheetContentComposing) {
                sheetDragHandle?.invoke()
            }
        },
        content = content,
    )
}
