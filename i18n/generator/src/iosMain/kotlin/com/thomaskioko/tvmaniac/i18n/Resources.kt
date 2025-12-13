@file:Suppress("unused")

package com.thomaskioko.tvmaniac.i18n

import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

public fun getString(stringResource: StringResource): StringDesc {
    return StringDesc.Resource(stringResource)
}

public fun getString(stringResource: StringResource, parameter: Any): StringDesc {
    return StringDesc.ResourceFormatted(stringResource, parameter)
}

public fun getPluralFormatted(pluralResource: PluralsResource, quantity: Int): StringDesc {
    return StringDesc.PluralFormatted(pluralResource, quantity, quantity)
}

public fun getPluralFormatted(pluralResource: PluralsResource, quantity: Int, vararg args: Any): StringDesc {
    return StringDesc.PluralFormatted(pluralResource, quantity, *args)
}
