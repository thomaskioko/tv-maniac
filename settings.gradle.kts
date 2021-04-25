pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }

}
rootProject.name = "tv-maniac"


include(
    ":androidApp",
    ":shared"
)
