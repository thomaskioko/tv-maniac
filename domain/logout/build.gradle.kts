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
                api(projects.core.base)
                api(projects.data.accountManager.api)
                api(projects.data.datastore.api)
                api(projects.data.logout.api)
                api(projects.data.user.api)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.accountManager.testing)
                implementation(projects.data.datastore.testing)
                implementation(projects.data.logout.testing)
                implementation(projects.data.user.testing)
            }
        }
    }
}
