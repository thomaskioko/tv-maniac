package checks

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin

apply<DetektPlugin>()


tasks.withType<Detekt>().configureEach {
    parallel = true
    setSource(files("src/main/kotlin"))
    config.setFrom(files("${rootProject.projectDir}/tooling/config/detekt.yml"))

    reports {
        html.required.set(true)
        html.outputLocation.set(rootProject.file("build/reports/detekt/report.html"))

        xml.required.set(true)
        xml.outputLocation.set(rootProject.file("build/reports/detekt/report.xml"))
    }

    exclude("resources/")
    exclude("build/")
}
