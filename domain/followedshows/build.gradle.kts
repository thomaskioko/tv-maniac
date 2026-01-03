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
                api(projects.data.followedshows.api)

                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.domain.showdetails)

                implementation(libs.coroutines.core)
                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.bundles.unittest)
                implementation(projects.core.util.testing)
                implementation(projects.data.followedshows.testing)
            }
        }
    }
}
