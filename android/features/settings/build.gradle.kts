plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    implementation(project(":shared:domain:trakt:api"))
    implementation(project( ":shared:domain:settings:api"))
    implementation(project( ":android:core:trakt-auth"))

    implementation(libs.accompanist.insetsui)

}
