plugins {
    id("tvmaniac.kmm.domain")
}


kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.core.traktAuth.api)
            implementation(projects.data.profile.api)
            implementation(projects.data.profilestats.api)

            implementation(libs.kotlinInject.runtime)
            implementation(libs.sqldelight.extensions)
        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.core.datastore.testing)
            implementation(projects.data.profile.testing)
            implementation(projects.core.traktAuth.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.presentation.profile"
}
