plugins {
    alias(libs.plugins.app.kmp)
}

kotlin { sourceSets { commonMain { dependencies { api(libs.coroutines.core) } } } }
