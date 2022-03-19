package checks

import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

apply<DetektPlugin>()

configure<DetektExtension> {

    parallel = true
    source = project.files("src/main/kotlin")
    config = files("${rootProject.projectDir}/tooling/config/detekt.yml")

    reports {
        html.required.set(true)
        html.outputLocation.set(rootProject.file("build/reports/detekt/report.html"))

        xml.required.set(true)
        xml.outputLocation.set(rootProject.file("build/reports/detekt/report.xml"))
    }
}
