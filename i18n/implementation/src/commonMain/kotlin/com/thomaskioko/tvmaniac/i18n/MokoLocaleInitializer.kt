package com.thomaskioko.tvmaniac.i18n

import com.thomaskioko.tvmaniac.core.base.AppInitializer
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class, multibinding = true)
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
