plugins {
    id("plugin.tvmaniac.multiplatform")
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.core.traktAuth.api)
                implementation(projects.data.profile.api)
                implementation(projects.data.profilestats.api)

                api(libs.decompose.decompose)
                api(libs.kotlinx.collections)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.sqldelight.extensions)
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
