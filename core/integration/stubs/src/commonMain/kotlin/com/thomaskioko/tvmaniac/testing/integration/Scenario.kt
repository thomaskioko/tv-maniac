package com.thomaskioko.tvmaniac.testing.integration

/**
 * @property provider Matches a `SyncProviderSource` entry name. Kept as a string so this module
 *   declares no project dependencies; the caller resolves it.
 */
public data class ScenarioAuthState(
    val provider: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Long = 3600,
)

public data class Scenario(
    val name: String,
    val authState: ScenarioAuthState? = null,
    val register: HttpScenarios.() -> Unit,
)

public object Scenarios {

    public const val UNAUTHENTICATED: String = "unauthenticated"
    public const val AUTHENTICATED_TRAKT: String = "authenticatedTrakt"

    private val all: List<Scenario> = listOf(
        Scenario(name = UNAUTHENTICATED) {
            stubBrowseGraph()
            stubTraktUsersMeUnauthorized()
        },
        Scenario(
            name = AUTHENTICATED_TRAKT,
            authState = ScenarioAuthState(
                provider = "TRAKT",
                accessToken = "ui-test-access",
                refreshToken = "ui-test-refresh",
            ),
        ) {
            stubBrowseGraph()
            stubTraktAuthenticatedEndpoints()
        },
    )

    public fun find(name: String): Scenario? = all.firstOrNull { it.name == name }

    public fun names(): List<String> = all.map { it.name }

    public fun apply(handler: MockEngineHandler, name: String): Scenario {
        val scenario = checkNotNull(find(name)) {
            "Unknown stub scenario \"$name\". Available: ${names().joinToString()}"
        }
        HttpScenarios(handler).apply(scenario.register)
        return scenario
    }
}
