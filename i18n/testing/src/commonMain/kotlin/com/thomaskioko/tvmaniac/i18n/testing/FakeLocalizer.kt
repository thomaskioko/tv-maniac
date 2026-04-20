package com.thomaskioko.tvmaniac.i18n.testing

import com.thomaskioko.tvmaniac.i18n.MokoResourcesLocalizer
import com.thomaskioko.tvmaniac.i18n.PluralsResourceKey
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.i18n.testing.util.getPlural
import com.thomaskioko.tvmaniac.i18n.testing.util.getString
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [MokoResourcesLocalizer::class])
public class FakeLocalizer : Localizer {

    override fun getString(key: StringResourceKey): String = key.getString()

    override fun getString(
        key: StringResourceKey,
        vararg args: Any,
    ): String = key.getString(*args)

    override fun getPlural(key: PluralsResourceKey, quantity: Int): String =
        key.getPlural(quantity)

    override fun getPlural(
        key: PluralsResourceKey,
        quantity: Int,
        vararg args: Any,
    ): String = key.getPlural(quantity, *args)
}
