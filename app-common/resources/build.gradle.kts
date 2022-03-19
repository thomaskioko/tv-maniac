import util.libs

plugins {
    id("com.android.library")
}

android {
    compileSdk = libs.versions.android.compile.get().toInt()
    namespace = "com.thomaskioko.tvmaniac.resources"

    defaultConfig {
        minSdk = libs.versions.android.min.get().toInt()
        targetSdk = libs.versions.android.target.get().toInt()
    }
}
