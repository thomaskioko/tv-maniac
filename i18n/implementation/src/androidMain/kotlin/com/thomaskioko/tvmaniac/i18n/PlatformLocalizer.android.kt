package com.thomaskioko.tvmaniac.i18n

import android.content.Context
import android.content.res.Configuration
import dev.icerock.moko.resources.desc.StringDesc
import me.tatarka.inject.annotations.Inject
import java.util.Locale

@Inject
actual class PlatformLocalizer(
    private val context: Context,
) {

    actual fun localized(stringDesc: StringDesc): String {
        val locale = Locale(Locale.getDefault().language)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        val localizedContext = context.createConfigurationContext(configuration)

        return stringDesc.toString(localizedContext)
    }
}
