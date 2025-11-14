package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
public class MokoLocaleInitializer(
    private val localeProvider: LocaleProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val applicationScope: CoroutineScope,
) : AppInitializer {
    private var localeJob: Job? = null

    override fun init() {
        localeJob = applicationScope.launch(dispatchers.main) {
            localeProvider.currentLocale.collect { locale ->
                StringDesc.localeType = StringDesc.LocaleType.Custom(locale)
            }
        }
    }
}
