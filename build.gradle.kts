buildscript {
    repositories.applyDefault()
}

allprojects {
    repositories.applyDefault()

    plugins.apply("checks.dependency-updates")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}