package com.thomaskioko.tvmaniac.navigation

/**
 * Marker for top-level destinations that anchor their own back stack.
 *
 * Bottom-tab roots (Discover, Library, Profile, Progress) implement [NavRoot]. Each registered
 * root contributes one [NavRootBinding] for polymorphic serialization, plus its instance into
 * `Set<NavRoot>` so [Navigator] can build the per-root back stack map at instantiation.
 *
 * Switching to a [NavRoot] is [Navigator.switchBackStack] (preserves the target's existing
 * stack) or [Navigator.showRoot] (clears it). Pushing onto the active root's stack uses
 * [Navigator.navigateTo] with a [NavRoute].
 */
public interface NavRoot : BaseRoute
