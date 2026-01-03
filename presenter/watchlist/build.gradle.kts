plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.core.util.api)
                implementation(projects.data.episode.api)
                implementation(projects.data.followedshows.api)
                implementation(projects.data.watchlist.api)
                implementation(projects.domain.episode)
                implementation(projects.domain.watchlist)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.core.util.testing)
                implementation(projects.data.episode.testing)
                implementation(projects.data.followedshows.testing)
                implementation(projects.data.watchlist.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
