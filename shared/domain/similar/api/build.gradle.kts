import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.api")
}


kotlin {
    android()
    ios()

    sourceSets {

        sourceSets["commonMain"].dependencies {
            implementation(project(":shared:domain:shows:api"))
        }

    }
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.similar.api"
}
