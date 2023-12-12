plugins {
    id("plugin.tvmaniac.multiplatform")
    alias(libs.plugins.ksp)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.util)
                implementation(projects.data.category.api)
                implementation(projects.data.shows.api)
                implementation(projects.data.popularshows.api)
                implementation(projects.data.topratedshows.api)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.upcomingshows.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(projects.data.shows.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}