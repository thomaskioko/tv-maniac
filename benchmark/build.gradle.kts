plugins {
    alias(libs.plugins.app.baseline.profile)
}

scaffold {
    explicitApi()

    benchmark {
        minSdkVersion(28)
        useManagedDevices()
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.benchmark.macro.junit4)
}
