package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.icerock.moko.resources.desc.StringDesc
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Inject
public class MokoLocaleInitializer(
    private val localeProvider: LocaleProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : AppInitializer {
    @OptIn(DelicateCoroutinesApi::class)
    override fun init() {
        GlobalScope.launch(dispatchers.main) {
            localeProvider.currentLocale.collect { locale ->
                StringDesc.localeType = StringDesc.LocaleType.Custom(locale)
            }
        }
    }
}
