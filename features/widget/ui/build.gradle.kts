plugins {
    alias(libs.plugins.app.android)
}

scaffold {
    useMetro()

    android {
        useCompose()
    }
}

dependencies {
    api(projects.core.base)
    api(projects.core.deeplink.api)
    api(projects.core.logger.api)
    api(projects.core.tasks.api)
    api(projects.domain.widget)

    api(libs.androidx.compose.runtime)
    api(libs.androidx.glance)
    api(libs.androidx.glance.appwidget)
    api(libs.coroutines.core)

    implementation(projects.androidDesignsystem)
    implementation(projects.core.testTags)
    implementation(projects.i18n.generator)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collections)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.glance.material3)
    implementation(libs.coil.base)
    implementation(libs.coil.coil)

    testImplementation(libs.androidx.compose.runtime)
    testImplementation(libs.androidx.glance.appwidget.testing)
    testImplementation(libs.androidx.glance.testing)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.bundles.unittest)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.robolectric)
}
