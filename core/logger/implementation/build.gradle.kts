plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    useKotlinInject()
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.base)
            implementation(projects.core.buildconfig.api)
            implementation(projects.core.logger.api)
            implementation(libs.kermit)
            implementation(libs.napier)
        }
    }
}
