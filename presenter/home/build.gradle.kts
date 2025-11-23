plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
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
                implementation(projects.presenter.discover)
                implementation(projects.presenter.search)
                implementation(projects.presenter.profile)
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
