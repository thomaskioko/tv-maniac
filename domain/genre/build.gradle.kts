plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.data.genre.api)

                implementation(projects.core.base)

                implementation(libs.coroutines.core)
                implementation(libs.metro.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.data.genre.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
