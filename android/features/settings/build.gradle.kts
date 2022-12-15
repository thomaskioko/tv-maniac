import util.libs

plugins {
    `android-feature-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.settings"
}

dependencies {
    api(project(":shared:core:util"))
    implementation(project(":shared:domain:trakt:api"))
    implementation(project( ":shared:domain:settings:api"))
    implementation(project(":android:core:compose"))
    implementation(project( ":android:core:trakt-auth"))

    implementation(libs.accompanist.insetsui)

}
