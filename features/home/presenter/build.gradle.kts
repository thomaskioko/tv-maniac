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
                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(projects.core.base)
                api(projects.domain.user)
                api(projects.features.home.nav)
                api(projects.navigation.api)

                implementation(projects.features.discover.nav)
                implementation(projects.features.library.nav)
                implementation(projects.features.profile.nav)
                implementation(projects.features.progress.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.integration.infra)
            }
        }
    }
}
