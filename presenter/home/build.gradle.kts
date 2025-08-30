plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
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
                implementation(projects.core.logger.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.discover)
                implementation(projects.domain.genre)
                implementation(projects.domain.watchlist)
                implementation(projects.presenter.discover)
                implementation(projects.presenter.search)
                implementation(projects.presenter.settings)
                implementation(projects.presenter.watchlist)

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
