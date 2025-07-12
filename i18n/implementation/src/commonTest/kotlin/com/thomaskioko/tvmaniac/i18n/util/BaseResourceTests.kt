package com.thomaskioko.tvmaniac.i18n.util

import com.thomaskioko.tvmaniac.i18n.api.Localizer
import com.thomaskioko.tvmaniac.i18n.testing.util.BaseLocalizerTest

expect abstract class BaseResourceTests() : BaseLocalizerTest {

    val localizer: Localizer
}
