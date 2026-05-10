package com.thomaskioko.tvmaniac.navigation

/**
 * Marks a value stored in the root modal sheet slot.
 *
 * Counterpart to [RootChild] for the single sheet overlay. Concrete implementations (most
 * commonly [SheetDestination]) wrap the sheet's presenter, and the root UI pattern-matches on the
 * child type to render the matching sheet content.
 */
public interface SheetChild
