plugins {
    id("plugin.tvmaniac.multiplatform")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.datastore.api)
                implementation(projects.data.profile.api)
                implementation(projects.core.traktAuth.api)

                api(libs.decompose.decompose)
                api(libs.essenty.lifecycle)

                implementation(libs.kotlinInject.runtime)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(projects.core.datastore.testing)
                implementation(projects.data.profile.testing)
                implementation(projects.core.traktAuth.testing)

                implementation(libs.bundles.unittest)
            }
        }
    }
}