plugins {
    alias(libs.plugins.app.kmp)
}

kotlin {
    sourceSets {

        androidMain.dependencies {
            api(projects.core.networkUtil.api)
            api(projects.data.accountManager.api)
        }
        commonMain {
            dependencies {
                api(projects.data.calendar.api)
                api(libs.coroutines.core)
            }
        }
    }
}
