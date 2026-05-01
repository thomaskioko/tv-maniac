package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * Pairs a [NavRoot] subclass with its generated [KSerializer].
 *
 * Each tab's `:nav` module contributes one [NavRootBinding] per [NavRoot] via `@Provides @IntoSet`
 * in a `@ContributesTo(ActivityScope::class) interface`. Contributions are collected as a
 * `Set<NavRootBinding<*>>` and folded into the polymorphic [NavRootSerializer] used by Decompose's
 * top-level `childStack` so the active-tab order survives process death.
 */
public data class NavRootBinding<T : NavRoot>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)
