
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class KotlinMultiplatformDomainPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("tvmaniac.kmm.library")
                apply("com.google.devtools.ksp")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig.targetSdk = 33
                compileSdk = 33
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }


            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {

                add("commonMainApi", libs.findLibrary("flowredux").get())
                add("commonMainImplementation", libs.findLibrary("coroutines-core").get())
                add("commonMainImplementation", libs.findLibrary("kotlinInject.runtime").get())

                add("commonTestImplementation", (kotlin("test")))
                add("commonTestImplementation", libs.findLibrary("coroutines-test").get())
                add("commonTestImplementation", libs.findLibrary("kotest-assertions").get())
                add("commonTestImplementation", libs.findLibrary("turbine").get())

            }
        }
    }
}