import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import com.thomaskioko.tvmaniac.extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                configureFlavors(this)
                defaultConfig.targetSdk = 33
            }

            configurations.configureEach {
                resolutionStrategy {}
            }
            dependencies {}
        }
    }
}
