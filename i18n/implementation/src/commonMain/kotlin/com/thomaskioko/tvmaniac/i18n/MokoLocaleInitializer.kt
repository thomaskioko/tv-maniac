package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.icerock.moko.resources.desc.StringDesc
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Inject
public class MokoLocaleInitializer(
    private val localeProvider: LocaleProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val applicationScope: CoroutineScope,
) : AppInitializer {
    private var localeJob: Job? = null

    override fun init() {
        localeJob = applicationScope.launch(dispatchers.main) {
            localeProvider.currentLocale
                .distinctUntilChanged()
                .collect { locale ->
                    StringDesc.localeType = StringDesc.LocaleType.Custom(locale)
                }
        }
    }
}
