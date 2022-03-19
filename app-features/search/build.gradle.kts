import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.search"
}

dependencies {
    implementation(libs.androidx.paging.runtime)
}
