package com.thomaskioko.tvmaniac.i18n

import dev.icerock.moko.resources.desc.StringDesc
import me.tatarka.inject.annotations.Inject

@Inject
public actual class PlatformLocalizer {
    public actual fun localized(stringDesc: StringDesc): String {
        return stringDesc.localized()
    }
}
