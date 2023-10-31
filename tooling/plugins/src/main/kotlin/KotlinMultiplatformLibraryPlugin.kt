import com.android.build.gradle.LibraryExtension
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import com.thomaskioko.tvmaniac.extensions.configureKotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class KotlinMultiplatformLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
            }

            extensions.configure<LibraryExtension> {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
                val sdkVersion = libs.findVersion("android-compileSdk")
                    .get().toString().toInt()

                configureKotlinMultiplatform(this)
                defaultConfig.targetSdk = sdkVersion
                configureFlavors(this)
            }

        }
    }
}