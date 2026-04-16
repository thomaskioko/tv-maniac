package com.thomaskioko.tvmaniac.navigation

import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

/**
 * Pairs a [SheetConfig] subclass with its generated [KSerializer].
 *
 * Features contribute one entry per sheet config via `@Provides @IntoSet` in the same DI module as
 * their [SheetChildFactory], typically inside a `@ContributesTo(ActivityScope::class) interface`.
 * The contributions are collected as a `Set<SheetConfigBinding<*>>` and folded into the
 * polymorphic [SheetConfigSerializer] used by Decompose's `childSlot` so the sheet slot survives
 * process death.
 */
public data class SheetConfigBinding<T : SheetConfig>(
    public val kClass: KClass<T>,
    public val serializer: KSerializer<T>,
)
