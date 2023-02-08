import org.jetbrains.kotlin.config.AnalysisFlags.optIn
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.library")
}


kotlin {
    android()
    ios()

    sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlin.time.ExperimentalTime")
            optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
            optIn("kotlinx.coroutines.FlowPreview")
        }
    }

    sourceSets {
        sourceSets["androidMain"].dependencies {

        }

        sourceSets["androidTest"].dependencies {

        }

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:core:util"))
            implementation(project(":shared:core:database"))
            implementation(project(":shared:domain:trailers:api"))

            implementation(libs.coroutines.core)
        }

        sourceSets["commonTest"].dependencies {

        }

        sourceSets["iosMain"].dependencies {

        }
    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.trailers.testing"
}