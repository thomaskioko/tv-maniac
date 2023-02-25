import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.domain.episodes.api"
}