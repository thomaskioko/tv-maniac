plugins { alias(libs.plugins.app.android) }

scaffold {
    android {
        useCompose()
    }

    optIn("androidx.compose.material3.ExperimentalMaterial3Api")
}

dependencies {
    api(libs.androidx.compose.ui.tooling)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.ui)
    api(libs.androidx.compose.material.icons)
    api(libs.androidx.compose.runtime)

    implementation(projects.data.datastore.api) // TODO:: RE-evaluate this dependency
    implementation(projects.i18n.generator)

    implementation(libs.androidx.annotation)
    implementation(libs.androidx.collections)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.coil.coil)
    implementation(libs.coil.compose)
    implementation(libs.kenburns)
    implementation(libs.androidx.palette)
    implementation(libs.coroutines.jvm)
    implementation(libs.kotlinx.collections)
}
