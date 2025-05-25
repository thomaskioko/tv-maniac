package com.thomaskioko.tvmaniac.i18n

import dev.icerock.moko.resources.desc.StringDesc

@Suppress("unused")
expect class PlatformLocalizer {

  /**
   * Localizes a string resource by its key.
   *
   * @param stringDesc The string description containing the resource key.
   * @return The localized string.
   */
  fun localized(stringDesc: StringDesc): String

}
