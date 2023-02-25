import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            api(project(":shared:data:trakt:api"))
            api(project(":shared:data:shows:api"))
            api(project(":shared:data:episodes:api"))
            api(libs.flowredux)

        }

        sourceSets["commonTest"].dependencies {
            implementation(project(":shared:data:trakt:testing"))
            implementation(project(":shared:data:tmdb:testing"))
            implementation(project(":shared:data:episodes:testing"))
            implementation(project(":shared:data:season-details:testing"))
            implementation(kotlin("test"))

            implementation(libs.testing.turbine)
            implementation(libs.testing.coroutines.test)
            implementation(libs.testing.kotest.assertions)
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.data.seasonepisodes.api"
}