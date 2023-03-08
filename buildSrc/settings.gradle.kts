enableFeaturePreview("VERSION_CATALOGS")
/**
 * Workaround to make version catalogs accessible from precompiled script plugins
 * https://github.com/gradle/gradle/issues/15383
 */
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
