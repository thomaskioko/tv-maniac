import util.libs

plugins {
    `android-compose-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.compose"
}

dependencies {
    implementation(libs.accompanist.insetsui)
}
