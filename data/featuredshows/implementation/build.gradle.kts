plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.api.tmdb.api)
                implementation(projects.core.base)
                implementation(projects.core.paging)
                implementation(projects.data.database.sqldelight)
                implementation(projects.core.util)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.requestManager.api)
                implementation(projects.data.trendingshows.api)

                api(libs.coroutines.core)

                implementation(libs.kotlinx.atomicfu)
                implementation(libs.sqldelight.extensions)
                implementation(libs.store5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.data.database.testing)
            }
        }
    }
}
