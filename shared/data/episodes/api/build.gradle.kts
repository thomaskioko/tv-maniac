plugins {
    id("tvmaniac.kmm.api")
}

kotlin {
    android()
    ios()
}

android {
    namespace = "com.thomaskioko.tvmaniac.episodes.api"
}