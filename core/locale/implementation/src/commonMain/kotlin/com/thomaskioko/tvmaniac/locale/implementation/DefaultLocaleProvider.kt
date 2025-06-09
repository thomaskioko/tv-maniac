package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLocaleProvider(
    private val platformLocaleProvider: PlatformLocaleProvider,
    private val datastoreRepository: DatastoreRepository,
) : LocaleProvider {

    override val currentLocale: Flow<String> = datastoreRepository.observeLanguage()

    override suspend fun setLocale(languageCode: String) {
        datastoreRepository.saveLanguage(languageCode)
    }

    override fun getPreferredLocales(): Flow<List<String>> {
        return platformLocaleProvider.getPreferredLocales()
    }
}
