import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import com.thomaskioko.tvmaniac.extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
                val sdkVersion = libs.findVersion("android-compileSdk")
                    .get().toString().toInt()

                configureKotlinAndroid(this)
                configureFlavors(this)
                defaultConfig.targetSdk = sdkVersion
            }

            configurations.configureEach {
                resolutionStrategy {}
            }
            dependencies {}
        }
    }
}
