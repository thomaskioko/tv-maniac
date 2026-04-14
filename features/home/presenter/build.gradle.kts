plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useMetro()
    useSerialization()

    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.navigation.api)
                implementation(projects.features.home.nav.api)
                implementation(projects.core.logger.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.user)

                implementation(libs.decompose.decompose)
                implementation(libs.essenty.lifecycle)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.core.testing.di)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
