package com.thomaskioko.tvmaniac.locale.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.locale.api.LocaleProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

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
