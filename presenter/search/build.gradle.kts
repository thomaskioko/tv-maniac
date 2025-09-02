plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
        "kotlinx.coroutines.FlowPreview",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.base)
                implementation(projects.core.util)
                implementation(projects.data.featuredshows.api)
                implementation(projects.data.trendingshows.api)
                implementation(projects.data.upcomingshows.api)
                implementation(projects.data.search.api)
                implementation(projects.data.genre.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.util.testing)
                implementation(projects.data.search.testing)
                implementation(projects.data.genre.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
