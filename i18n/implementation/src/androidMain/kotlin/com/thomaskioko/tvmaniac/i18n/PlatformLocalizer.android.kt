package com.thomaskioko.tvmaniac.i18n

import android.content.Context
import android.content.res.Configuration
import com.thomaskioko.tvmaniac.core.base.di.ApplicationContext
import dev.icerock.moko.resources.desc.StringDesc
import dev.zacsweers.metro.Inject
import java.util.Locale

@Inject
public actual class PlatformLocalizer(
    @ApplicationContext private val context: Context,
) {

    public actual fun localized(stringDesc: StringDesc): String {
        val locale = Locale(Locale.getDefault().language)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        val localizedContext = context.createConfigurationContext(configuration)

        return stringDesc.toString(localizedContext)
    }
}
