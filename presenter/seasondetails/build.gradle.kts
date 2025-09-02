plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
    useSerialization()

    optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.core.view)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.data.seasondetails.api)
                implementation(projects.domain.seasondetails)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(projects.core.logger.testing)
                implementation(projects.data.seasondetails.testing)
                implementation(projects.data.cast.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
