plugins {
    alias(libs.plugins.app.baseline.profile)
}

scaffold {
    benchmark {
        minSdkVersion(28)
        useManagedDevices()
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.benchmark.macro.junit4)
}
