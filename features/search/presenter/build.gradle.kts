plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(projects.core.base)
                api(projects.core.logger.api)
                api(projects.core.util.api)
                api(projects.core.view)
                api(projects.data.genre.api)
                api(projects.data.search.api)
                api(projects.domain.genre)
                api(projects.features.search.nav)
                api(projects.i18n.api)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.genre.testing)
                implementation(projects.data.search.testing)
                implementation(projects.data.shows.api)
                implementation(projects.i18n.generator)
                implementation(projects.i18n.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
