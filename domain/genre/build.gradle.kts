plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

tvmaniac {
    useDependencyInjection()
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
