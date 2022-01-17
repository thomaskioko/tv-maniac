import util.libs

plugins {
    `android-feature-plugin`
}

dependencies {
    implementation(project(":shared:domain:show:api"))
    implementation(libs.androidx.paging.runtime)
}
