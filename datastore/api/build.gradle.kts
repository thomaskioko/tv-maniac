plugins { alias(libs.plugins.tvmaniac.multiplatform) }

kotlin { sourceSets { commonMain { dependencies { api(libs.coroutines.core) } } } }
