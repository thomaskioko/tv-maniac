package com.thomaskioko.tvmaniac.navigation

/**
 * Marks a top-level destination that anchors its own back stack.
 *
 * Bottom-tab roots (Discover, Library, Profile, Progress) implement [NavRoot]. Each registered
 * root contributes one [NavRootBinding] for polymorphic serialization, plus its own instance into
 * `Set<NavRoot>` so [Navigator] can build the back stack map for each root at instantiation.
 *
 * Switching to a [NavRoot] uses [Navigator.switchBackStack] (preserves the target's existing
 * stack) or [Navigator.showRoot] (clears it). Pushing onto the active root's stack uses
 * [Navigator.navigateTo] with a [NavRoute].
 */
public interface NavRoot : BaseRoute

/**
 * Returns a stable identifier suitable for keying UI state to a tab anchor.
 *
 * Mirrors the `KSerializer.descriptor.serialName` produced by the kotlinx.serialization plugin
 * for `@Serializable data object` declarations. The polymorphic `NavRoot` serializer composed
 * from each [NavRootBinding] uses the same identity to persist and restore the active tab and
 * its back stack across configuration change and process death, so UI scopes keyed off
 * [stableKey] line up with the saved navigation state.
 */
public val NavRoot.stableKey: String
    get() = this::class.qualifiedName
        ?: error("NavRoot ${this::class.simpleName} has no qualified name; declare it as a top-level class.")
