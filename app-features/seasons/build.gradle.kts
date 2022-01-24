import util.libs

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(libs.androidx.paging.runtime)
    implementation(project(":shared:domain:seasons:api"))
    implementation(project(":shared:domain:episodes:api"))
}
