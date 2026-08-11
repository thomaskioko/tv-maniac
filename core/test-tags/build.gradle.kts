plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    addAndroidTarget()
    addIosTargetsWithXcFramework(frameworkName = "TvManiacTestTags") { framework ->
        framework.isStatic = true
    }
}
