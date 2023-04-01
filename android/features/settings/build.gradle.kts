plugins {
    id("tvmaniac.android.feature")
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    implementation(project(":android:core:trakt-auth"))
    implementation(project(":shared:data:datastore:api"))
    implementation(project(":shared:data:shows:api"))
    implementation(project(":shared:domain:settings"))

    implementation(libs.accompanist.insetsui)
    implementation(libs.flowredux)

}
