import util.libs

plugins {
    `kmm-domain-plugin`
}

android {
    namespace = "com.thomaskioko.tvmaniac.core.test"
}

dependencies {
    commonMainImplementation(libs.coroutines.core)
}
