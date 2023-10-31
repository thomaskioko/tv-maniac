import com.android.build.api.dsl.ApplicationExtension
import com.thomaskioko.tvmaniac.extensions.TvManiacFlavor
import com.thomaskioko.tvmaniac.extensions.configureAndroidCompose
import com.thomaskioko.tvmaniac.extensions.configureFlavors
import com.thomaskioko.tvmaniac.extensions.configureKotlinAndroid
import com.thomaskioko.tvmaniac.extensions.FlavorDimension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

@Suppress("UnstableApiUsage")
class ApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
                val sdkVersion = libs.findVersion("android-compileSdk")
                    .get().toString().toInt()

                defaultConfig {
                    targetSdk = sdkVersion
                    missingDimensionStrategy(
                        FlavorDimension.contentType.name,
                        TvManiacFlavor.demo.name
                    )
                }

                buildFeatures {
                    buildConfig = true
                }

                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                configureFlavors(this)
            }
        }
    }
}
