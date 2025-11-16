package com.thomaskioko.tvmaniac.buildconfig.api

public interface BuildConfigRepository {

    /**
     * Retrieves the TMDB API key from secure storage.
     * @return The API key if configured, null otherwise
     */
    public suspend fun getTmdbApiKey(): String?

    /**
     * Retrieves the Trakt OAuth client ID from secure storage.
     * @return The client ID if configured, null otherwise
     */
    public suspend fun getTraktClientId(): String?

    /**
     * Retrieves the Trakt OAuth client secret from secure storage.
     * @return The client secret if configured, null otherwise
     */
    public suspend fun getTraktClientSecret(): String?

    /**
     * Stores the TMDB API key in secure storage.
     * @param key The API key to store
     */
    public suspend fun setTmdbApiKey(key: String)

    /**
     * Stores the Trakt OAuth client ID in secure storage.
     * @param id The client ID to store
     */
    public suspend fun setTraktClientId(id: String)

    /**
     * Stores the Trakt OAuth client secret in secure storage.
     * @param secret The client secret to store
     */
    public suspend fun setTraktClientSecret(secret: String)

    /**
     * Removes all configuration data from secure storage.
     * Useful for testing, logout scenarios, or resetting the app state.
     */
    public suspend fun clearAll()

    /**
     * Checks if all required configuration is present in secure storage.
     * @return true if TMDB API key, Trakt client ID, and client secret are all configured
     */
    public suspend fun isConfigured(): Boolean
}
