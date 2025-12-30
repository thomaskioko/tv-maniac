package com.thomaskioko.tvmaniac.i18n.api

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey

public interface Localizer {
    /**
     * Returns a localized string for the given [StringResourceKey].
     *
     * @param key The key for the string resource.
     * @return The localized string.
     */
    public fun getString(key: StringResourceKey): String

    /**
     * Returns a localized string for the given [StringResourceKey] with optional arguments.
     *
     * @param key The key for the string resource.
     * @param args Optional arguments to format the string.
     * @return The localized string formatted with the provided arguments.
     */
    public fun getString(key: StringResourceKey, vararg args: Any): String

    /**
     * Returns a localized plural string for the given [PluralsResourceKey] based on the specified quantity.
     *
     * @param key The key for the plural resource.
     * @param quantity The quantity to determine the correct plural form.
     * @return The localized plural string formatted with the provided arguments.
     */
    public fun getPlural(key: PluralsResourceKey, quantity: Int): String

    /**
     * Returns a localized plural string for the given [PluralsResourceKey] based on the specified quantity.
     *
     * @param key The key for the plural resource.
     * @param quantity The quantity to determine the correct plural form.
     * @param args Optional arguments to format the plural string.
     * @return The localized plural string formatted with the provided arguments.
     */
    public fun getPlural(key: PluralsResourceKey, quantity: Int, vararg args: Any): String
}
