import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(project(":shared:domain:trakt:api"))
            api(project(":shared:domain:shows:api"))
            api(project(":shared:domain:episodes:api"))
            api(libs.flowredux)

        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:domain:trakt:testing"))
            implementation(project(":shared:domain:tmdb:testing"))
            implementation(project(":shared:domain:episodes:testing"))
            implementation(project(":shared:domain:season-details:testing"))
            implementation(kotlin("test"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.seasonepisodes.api"
}