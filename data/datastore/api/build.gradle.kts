plugins {
    alias(libs.plugins.tvmaniac.kmp)
}

kotlin { sourceSets { commonMain { dependencies { api(libs.coroutines.core) } } } }
