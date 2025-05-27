package com.thomaskioko.tvmaniac.i18n

import android.content.Context
import dev.icerock.moko.resources.desc.StringDesc
import me.tatarka.inject.annotations.Inject

@Inject
actual class PlatformLocalizer(
    private val context: Context,
) {

    actual fun localized(stringDesc: StringDesc): String {
        return stringDesc.toString(context)
    }
}
