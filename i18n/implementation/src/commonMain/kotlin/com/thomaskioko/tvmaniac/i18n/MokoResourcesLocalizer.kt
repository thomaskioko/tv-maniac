package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.api.Localizer
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class MokoResourcesLocalizer(
    private val platformLocalizer: PlatformLocalizer,
) : Localizer {
    override fun getString(key: StringResourceKey): String {
        return platformLocalizer.localized(key.resourceId.desc())
    }

    override fun getString(key: StringResourceKey, vararg args: Any): String {
        return platformLocalizer.localized(key.resourceId.format(*args))
    }

    override fun getPlural(key: PluralsResourceKey, quantity: Int): String {
        return platformLocalizer.localized(key.resourceId.format(quantity, quantity))
    }

    override fun getPlural(key: PluralsResourceKey, quantity: Int, vararg args: Any): String {
        return platformLocalizer.localized(key.resourceId.format(quantity, *args))
    }
}
