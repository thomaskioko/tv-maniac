package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.api.Localizer
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

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
