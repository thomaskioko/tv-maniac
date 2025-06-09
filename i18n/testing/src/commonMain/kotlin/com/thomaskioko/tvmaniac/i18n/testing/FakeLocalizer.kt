package com.thomaskioko.tvmaniac.i18n.testing

import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.i18n.testing.util.getPlural
import com.thomaskioko.tvmaniac.i18n.testing.util.getString

public class FakeLocalizer : Localizer {

    override fun getString(key: StringResourceKey): String = key.getString()

    override fun getString(
        key: StringResourceKey,
        vararg args: Any,
    ): String = key.getString(args)

    override fun getPlural(key: PluralsResourceKey, quantity: Int): String =
        key.getPlural(quantity)

    override fun getPlural(
        key: PluralsResourceKey,
        quantity: Int,
        vararg args: Any,
    ): String = key.getPlural(quantity, args)
}
