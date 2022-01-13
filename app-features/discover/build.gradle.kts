import util.libs

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:discover:api"))
    implementation(libs.androidx.paging.runtime)
}
