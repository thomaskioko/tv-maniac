plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.data.traktauth.api)
                api(projects.data.traktlists.api)
                api(projects.data.user.api)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
            }
        }
    }
}
