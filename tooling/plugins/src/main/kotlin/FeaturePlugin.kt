import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("tvmaniac.compose.library")
                apply("tvmaniac.hilt")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    manifestPlaceholders["appAuthRedirectScheme"] = "empty"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                add("api", project(":shared:core:util"))
                add("api", project(":android:core:designsystem"))
                add("api", project(":android:core:navigation"))
                add("implementation", project(":android:core:resources"))

                add("api", libs.findLibrary("androidx.navigation.common").get())
                add("api", libs.findLibrary("androidx.navigation.runtime").get())

                add("implementation", libs.findLibrary("androidx.compose.foundation").get())
                add("implementation", libs.findLibrary("androidx.hilt.navigationcompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("coroutines.core").get())

                add("runtimeOnly", libs.findLibrary("coroutines.android").get())
            }
        }
    }
}
