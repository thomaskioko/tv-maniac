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
                implementation(projects.shared.core.database)
                implementation(projects.shared.core.datastore.implementation)
                implementation(projects.shared.core.networkutil)
                implementation(projects.shared.core.traktApi.api)
                implementation(projects.shared.core.traktApi.implementation)
                implementation(projects.shared.core.tmdbApi.api)
                implementation(projects.shared.core.tmdbApi.implementation)
                implementation(projects.shared.core.util)

                implementation(projects.shared.domain.discover)
                implementation(projects.shared.domain.following)
                implementation(projects.shared.domain.seasondetails)
                implementation(projects.shared.domain.settings)
                implementation(projects.shared.domain.seasondetails)
                implementation(projects.shared.domain.showDetails)
                implementation(projects.shared.domain.trailers)

                implementation(projects.shared.data.category.implementation)
                implementation(projects.shared.data.episodes.implementation)
                implementation(projects.shared.data.profile.implementation)
                implementation(projects.shared.data.similar.implementation)
                implementation(projects.shared.data.seasonDetails.implementation)
                implementation(projects.shared.data.shows.implementation)
                implementation(projects.shared.data.trailers.implementation)

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
