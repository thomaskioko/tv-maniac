package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.i18n.api.Localizer
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class MokoResourcesLocalizer(
  private val platformLocalizer: PlatformLocalizer,
) : Localizer {
  override fun getString(key: StringResourceKey): String {
    return platformLocalizer.localized(StringDesc.Resource(key.resourceId))
  }

  override fun getString(key: StringResourceKey, vararg args: Any): String {
    return platformLocalizer.localized(StringDesc.ResourceFormatted(key.resourceId, *args))
  }

  override fun getPlural(key: PluralsResourceKey, quantity: Int): String {
    return platformLocalizer.localized(StringDesc.PluralFormatted(key.resourceId, quantity, quantity))
  }

  override fun getPlural(key: PluralsResourceKey, quantity: Int, vararg args: Any): String {
    return platformLocalizer.localized(StringDesc.PluralFormatted(key.resourceId, quantity, *args))
  }
}

