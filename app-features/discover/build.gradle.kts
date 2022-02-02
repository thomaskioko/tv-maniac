import util.libs

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:show:api"))
    implementation(project(":shared:domain:show-common:api"))
    implementation(libs.androidx.paging.runtime)
}
