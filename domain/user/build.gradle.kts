plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.user.api)
                api(projects.data.traktauth.api)
                api(projects.core.util)

                implementation(projects.core.base)
                implementation(projects.domain.logout)

                implementation(libs.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.user.testing)
                implementation(projects.data.traktauth.testing)
            }
        }
    }
}
