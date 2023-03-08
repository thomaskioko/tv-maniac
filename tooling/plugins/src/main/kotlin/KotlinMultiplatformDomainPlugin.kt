
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
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig.targetSdk = 33
                compileSdk = 33
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }


            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {

                add("commonMainApi", libs.findLibrary("flowredux").get())
                add("commonMainImplementation", libs.findLibrary("coroutines-core").get())
                add("commonMainImplementation", libs.findLibrary("koin").get())

                add("commonTestImplementation", (kotlin("test")))
                add("commonTestImplementation", libs.findLibrary("testing-coroutines-test").get())
                add("commonTestImplementation", libs.findLibrary("testing-kotest-assertions").get())
                add("commonTestImplementation", libs.findLibrary("testing-turbine").get())

            }
        }
    }
}