@file:Suppress("unused")

package com.thomaskioko.tvmaniac.i18n

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

fun getString(stringResource: StringResource): StringDesc {
  return StringDesc.Resource(stringResource)
}

fun getString(stringResource: StringResource, parameter: Any): StringDesc {
  return StringDesc.ResourceFormatted(stringResource, parameter)
}

fun getPlural(pluralResource: PluralsResource, quantity: Int): StringDesc {
  return StringDesc.PluralFormatted(pluralResource, quantity)
}