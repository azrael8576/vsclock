import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.wei.vsclock.configureFlavors
import com.wei.vsclock.configureGradleManagedDevices
import com.wei.vsclock.configureKotlinAndroid
import com.wei.vsclock.configurePrintApksTask
import com.wei.vsclock.disableUnnecessaryAndroidTests
import com.wei.vsclock.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("vsclock.android.lint")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                configureFlavors(this)
                configureGradleManagedDevices(this)
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                add("testImplementation", kotlin("test"))

                // Paging 3
                add("implementation", libs.findLibrary("paging-runtime").get())
                add("implementation", libs.findLibrary("paging-compose").get())
                add("testImplementation", libs.findLibrary("paging-common").get())

                // Timber
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}
