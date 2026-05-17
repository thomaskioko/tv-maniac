plugins {
    alias(libs.plugins.app.kmp)
}

scaffold {
    optIn(
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )
}
