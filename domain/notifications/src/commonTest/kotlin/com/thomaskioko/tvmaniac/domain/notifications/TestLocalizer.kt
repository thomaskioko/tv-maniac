package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer

internal class TestLocalizer(private val failure: Throwable? = null) : Localizer {

    override fun getString(key: StringResourceKey): String = key.toString()

    override fun getString(key: StringResourceKey, vararg args: Any): String {
        failure?.let { throw it }
        return "$key(${args.joinToString(",")})"
    }

    override fun getPlural(key: PluralsResourceKey, quantity: Int): String = "$key($quantity)"

    override fun getPlural(key: PluralsResourceKey, quantity: Int, vararg args: Any): String =
        "$key($quantity,${args.joinToString(",")})"
}
