package com.thomaskioko.tvmaniac.testing.integration

public data class Scenario(
    val name: String,
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
        Scenario(name = AUTHENTICATED_TRAKT) {
            stubBrowseGraph()
            stubTraktAuthenticatedEndpoints()
        },
    )

    public fun find(name: String): Scenario? = all.firstOrNull { it.name == name }

    public fun names(): List<String> = all.map { it.name }

    public fun apply(handler: MockEngineHandler, name: String) {
        val scenario = checkNotNull(find(name)) {
            "Unknown stub scenario \"$name\". Available: ${names().joinToString()}"
        }
        HttpScenarios(handler).apply(scenario.register)
    }
}
