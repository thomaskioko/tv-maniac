import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class KotlinMultiplatformDataImplPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("tvmaniac.kmm.library")
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }


            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {

                add("commonMainImplementation", (project(":shared:core:util")))
                add("commonMainImplementation", (project(":shared:data:database")))
                add("commonMainImplementation", libs.findLibrary("kermit").get())
                add("commonMainImplementation", libs.findLibrary("koin").get())

            }
        }
    }
}