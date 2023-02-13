import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KotlinMultiplatformDomainApiPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("tvmaniac.kmm.library")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("commonMainApi",(project(":shared:core:util")))
                add("commonMainApi",(project(":shared:core:database")))
                add("commonMainImplementation", libs.findLibrary("coroutines.core").get())
            }
        }
    }
}