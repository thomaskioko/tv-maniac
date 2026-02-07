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
                implementation(projects.data.episode.api)
                implementation(projects.data.upnext.api)
                implementation(projects.data.traktauth.api)
                implementation(projects.core.base)
                implementation(projects.core.logger.api)
                implementation(projects.domain.episode)
                implementation(projects.domain.upnext)
                implementation(projects.domain.library)

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
                implementation(projects.data.upnext.testing)
                implementation(projects.data.traktauth.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}
