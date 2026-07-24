package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer

internal class TestLocalizer : Localizer {

    override fun getString(key: StringResourceKey): String = key.toString()

    override fun getString(key: StringResourceKey, vararg args: Any): String =
        "$key(${args.joinToString(",")})"

    override fun getPlural(key: PluralsResourceKey, quantity: Int): String = "$key($quantity)"

    override fun getPlural(key: PluralsResourceKey, quantity: Int, vararg args: Any): String =
        "$key($quantity,${args.joinToString(",")})"
}
