plugins {
    id("tvmaniac.kmm.domain")
}

kotlin {
    android()
    ios()

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(projects.core.util)
            implementation(projects.data.category.api)
            implementation(projects.data.showimages.api)
            implementation(projects.data.shows.api)

            implementation(libs.kotlinx.collections)
        }

        sourceSets["commonTest"].dependencies {
            implementation(projects.data.showimages.testing)
            implementation(projects.data.shows.testing)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.presentation.discover"
}
