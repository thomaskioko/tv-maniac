plugins { id("plugin.tvmaniac.multiplatform") }

kotlin { sourceSets { commonMain { dependencies { implementation(projects.traktAuth.api) } } } }
