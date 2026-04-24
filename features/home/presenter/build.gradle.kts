plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useSerialization()
    useCodegen()

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
                implementation(projects.features.home.nav)
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
                implementation(projects.core.integration.infra)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
