package com.thomaskioko.tvmaniac.buildconfig.testing

import com.thomaskioko.tvmaniac.buildconfig.api.BuildConfigRepository

public class FakeBuildConfigRepository : BuildConfigRepository {

    private val storage = mutableMapOf<String, String>().apply {
        put(KEY_TMDB_API_KEY, "fake_tmdb_api_key_for_testing")
        put(KEY_TRAKT_CLIENT_ID, "fake_trakt_client_id_for_testing")
        put(KEY_TRAKT_CLIENT_SECRET, "fake_trakt_client_secret_for_testing")
    }

    override suspend fun getTmdbApiKey(): String? = storage[KEY_TMDB_API_KEY]

    override suspend fun getTraktClientId(): String? = storage[KEY_TRAKT_CLIENT_ID]

    override suspend fun getTraktClientSecret(): String? = storage[KEY_TRAKT_CLIENT_SECRET]

    override suspend fun setTmdbApiKey(key: String) {
        storage[KEY_TMDB_API_KEY] = key
    }

    override suspend fun setTraktClientId(id: String) {
        storage[KEY_TRAKT_CLIENT_ID] = id
    }

    override suspend fun setTraktClientSecret(secret: String) {
        storage[KEY_TRAKT_CLIENT_SECRET] = secret
    }

    override suspend fun clearAll() {
        storage.clear()
    }

    override suspend fun isConfigured(): Boolean {
        return storage.containsKey(KEY_TMDB_API_KEY) &&
            storage.containsKey(KEY_TRAKT_CLIENT_ID) &&
            storage.containsKey(KEY_TRAKT_CLIENT_SECRET)
    }

    private companion object {
        private const val KEY_TMDB_API_KEY = "tmdb_api_key"
        private const val KEY_TRAKT_CLIENT_ID = "trakt_client_id"
        private const val KEY_TRAKT_CLIENT_SECRET = "trakt_client_secret"
    }
}
