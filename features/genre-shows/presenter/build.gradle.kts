plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useCodegen()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.base)
                api(projects.data.genre.api)
                api(projects.features.genreShows.nav)
                api(projects.navigation.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                api(projects.core.view)
                implementation(projects.data.shows.api)
                implementation(projects.features.showDetails.nav)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.genre.testing)
                implementation(projects.navigation.testing)
            }
        }
    }
}
