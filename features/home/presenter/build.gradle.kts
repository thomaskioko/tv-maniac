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
                implementation(projects.core.logger.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.discover)
                implementation(projects.domain.genre)
                implementation(projects.features.discover.presenter)
                implementation(projects.features.profile.presenter)
                implementation(projects.domain.user)
                implementation(projects.features.library.presenter)
                implementation(projects.features.progress.presenter)

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
