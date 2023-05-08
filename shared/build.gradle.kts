plugins {
    id("tvmaniac.kmm.library")
    id("maven-publish")
    alias(libs.plugins.kmmbridge)
    alias(libs.plugins.ksp)
}

version = libs.versions.shared.module.version.get()

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "TvManiac"
            isStatic = true
            linkerOpts.add("-lsqlite3")
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.core.database)
                implementation(projects.core.datastore.implementation)
                implementation(projects.core.networkutil)
                implementation(projects.core.traktApi.api)
                implementation(projects.core.traktApi.implementation)
                implementation(projects.core.tmdbApi.api)
                implementation(projects.core.tmdbApi.implementation)
                implementation(projects.core.util)

                implementation(projects.presentation.discover)
                implementation(projects.presentation.following)
                implementation(projects.presentation.seasondetails)
                implementation(projects.presentation.settings)
                implementation(projects.presentation.seasondetails)
                implementation(projects.presentation.showDetails)
                implementation(projects.presentation.trailers)

                implementation(projects.data.category.implementation)
                implementation(projects.data.episodes.implementation)
                implementation(projects.data.profile.implementation)
                implementation(projects.data.similar.implementation)
                implementation(projects.data.seasonDetails.implementation)
                implementation(projects.data.shows.implementation)
                implementation(projects.data.trailers.implementation)

                implementation(libs.kotlinInject.runtime)
            }
        }

        val commonTest by getting
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }
    }
}

dependencies {
    add("kspIosX64", libs.kotlinInject.compiler)
    add("kspIosArm64", libs.kotlinInject.compiler)
}

android {
    namespace = "com.thomaskioko.tvmaniac.shared.base"
}

addGithubPackagesRepository()

kmmbridge {
    frameworkName.set("TvManiac")
    mavenPublishArtifacts()
    githubReleaseVersions()
    spm()
    versionPrefix.set("0.2.5")
}
