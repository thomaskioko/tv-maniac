plugins {
    `kmm-domain-plugin`
}

kotlin {

    sourceSets {
        sourceSets["commonMain"].dependencies {
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.ktor.core)
            implementation(libs.kermit)
        }

        sourceSets["iosMain"].dependencies {
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.koin.core)
        }
    }
}
