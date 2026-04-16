package com.thomaskioko.tvmaniac.navigation

/**
 * Marker interface for the value stored in the root modal sheet slot.
 *
 * Counterpart to [RootChild] for the single sheet overlay. Concrete implementations (most
 * commonly [SheetDestination]) wrap the sheet's presenter. The root UI pattern-matches on the
 * child type to render the matching sheet content.
 */
public interface SheetChild
