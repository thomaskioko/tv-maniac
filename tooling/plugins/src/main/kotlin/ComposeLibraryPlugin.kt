import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.configureAndroidCompose
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ComposeLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {

            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 33
                configureAndroidCompose(this)
                configureFlavors(this)
            }
        }
    }
}
