plugins { id("plugin.tvmaniac.multiplatform") }

kotlin { sourceSets { commonMain.dependencies { implementation(libs.kotlinInject.runtime) } } }
