package com.thomaskioko.tvmaniac.buildconfig.implementation

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfigRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class AndroidBuildConfigRepository(context: Context) : BuildConfigRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override suspend fun getTmdbApiKey(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_TMDB_API_KEY, null)
    }

    override suspend fun getTraktClientId(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_TRAKT_CLIENT_ID, null)
    }

    override suspend fun getTraktClientSecret(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_TRAKT_CLIENT_SECRET, null)
    }

    override suspend fun setTmdbApiKey(key: String): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_TMDB_API_KEY, key)
            .apply()
    }

    override suspend fun setTraktClientId(id: String): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_TRAKT_CLIENT_ID, id)
            .apply()
    }

    override suspend fun setTraktClientSecret(secret: String): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .putString(KEY_TRAKT_CLIENT_SECRET, secret)
            .apply()
    }

    override suspend fun clearAll(): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    override suspend fun isConfigured(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.contains(KEY_TMDB_API_KEY) &&
            sharedPreferences.contains(KEY_TRAKT_CLIENT_ID) &&
            sharedPreferences.contains(KEY_TRAKT_CLIENT_SECRET)
    }

    private companion object {
        private const val PREFS_FILE_NAME = "tvmaniac_secure_config"
        private const val KEY_TMDB_API_KEY = "tmdb_api_key"
        private const val KEY_TRAKT_CLIENT_ID = "trakt_client_id"
        private const val KEY_TRAKT_CLIENT_SECRET = "trakt_client_secret"
    }
}
