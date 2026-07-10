package com.thomaskioko.tvmaniac.accountmanager.api

public interface AuthClientConfig : SyncProvider {
    public val clientId: String
    public val clientSecret: String
    public val redirectUri: String
    public val authorizationEndpoint: String
    public val tokenEndpoint: String
    public val scopes: List<String>
}
