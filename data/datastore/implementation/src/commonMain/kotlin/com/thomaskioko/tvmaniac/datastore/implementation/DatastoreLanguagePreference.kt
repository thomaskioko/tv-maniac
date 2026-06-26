package com.thomaskioko.tvmaniac.datastore.implementation

import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.locale.api.LanguagePreference
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DatastoreLanguagePreference(
    private val datastoreRepository: DatastoreRepository,
) : LanguagePreference {
    override fun observeLanguage(): Flow<String> = datastoreRepository.observeLanguage()

    override suspend fun saveLanguage(languageCode: String) {
        datastoreRepository.saveLanguage(languageCode)
    }
}
