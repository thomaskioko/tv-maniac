package com.thomaskioko.tvmaniac.navigation.ui

import androidx.compose.runtime.Composable
import com.thomaskioko.tvmaniac.navigation.SheetChild

/**
 * Platform-side renderer for a [SheetChild] in the root modal sheet slot.
 *
 * Sheet counterpart to [ScreenContent]. Each sheet-owning feature `ui` module contributes one
 * [SheetContent] through `@ContributesTo(ActivityScope::class)`. The root Compose consumer
 * iterates the injected `Set<SheetContent>`, picks the first whose [matches] returns `true`, and
 * invokes [content] to render the sheet.
 *
 * @property matches predicate that returns `true` when this content can render the given child.
 * @property content composable invoked with the matched child.
 */
public class SheetContent(
    public val matches: (SheetChild) -> Boolean,
    public val content: @Composable (SheetChild) -> Unit,
)
