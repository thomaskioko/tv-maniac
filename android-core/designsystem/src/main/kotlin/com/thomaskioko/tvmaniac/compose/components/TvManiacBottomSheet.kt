package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvManiacBottomSheetScaffold(
    showBottomSheet: Boolean,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (PaddingValues) -> Unit,
    onDismissBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
    skipHiddenState: Boolean = false,
    sheetShadowElevation: Dp = 8.dp,
    initialSheetState: SheetValue = SheetValue.Hidden,
    sheetShape: Shape = RoundedCornerShape(5.dp),
) {
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = initialSheetState,
        skipHiddenState = skipHiddenState,
    )
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState,
    )

    LaunchedEffect(key1 = showBottomSheet) {
        if (showBottomSheet) {
            bottomSheetState.expand()
        } else {
            bottomSheetState.hide()
        }
    }

    BottomSheetScaffold(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (bottomSheetState.isVisible) onDismissBottomSheet()
                })
            },
        scaffoldState = bottomSheetScaffoldState,
        sheetShape = sheetShape,
        sheetShadowElevation = sheetShadowElevation,
        sheetContent = sheetContent,
        content = content,
    )
}
