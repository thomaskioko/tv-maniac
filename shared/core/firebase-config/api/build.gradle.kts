plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.core.firebaseconfig.api"
}

dependencies {

    androidMainImplementation(platform(libs.firebase.bom))
    androidMainImplementation(libs.firebase.config)
}
