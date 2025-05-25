package com.thomaskioko.tvmaniac.i18n

import dev.icerock.moko.resources.desc.StringDesc
import me.tatarka.inject.annotations.Inject

@Inject
actual class PlatformLocalizer {
  actual fun localized(stringDesc: StringDesc): String {
    return stringDesc.localized()
  }
}
