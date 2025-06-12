package com.thomaskioko.tvmaniac.i18n.util

import com.thomaskioko.tvmaniac.i18n.MokoResourcesLocalizer
import com.thomaskioko.tvmaniac.i18n.PlatformLocalizer
import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.i18n.testing.util.BaseLocalizerTest

actual abstract class BaseResourceTests : BaseLocalizerTest() {

    actual val localizer: Localizer
        get() = MokoResourcesLocalizer(PlatformLocalizer())
}
