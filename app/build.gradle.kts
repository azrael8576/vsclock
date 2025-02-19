import com.wei.vsclock.VscBuildType

plugins {
    alias(libs.plugins.vsclock.android.application)
    alias(libs.plugins.vsclock.android.application.compose)
    alias(libs.plugins.vsclock.android.application.flavors)
    alias(libs.plugins.vsclock.android.hilt)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.wei.vsclock"

    defaultConfig {
        applicationId = "com.wei.vsclock"
        /**
         * Version Code: AABCXYZ
         *
         * AA: API Level (35)
         *
         * BC: Supported screen sizes for this APK.
         * 12: Small to Normal screens
         * 34: Large to X-Large screens
         *
         * XYZ: App version (050 for 0.5.0)
         */
        versionCode = 3514000
        /**
         * SemVer major.minor.patch
         */
        versionName = "0.0.0"

        // Custom test runner to set up Hilt dependency graph
//        testInstrumentationRunner = "com.wei.vsclock.core.testing.VscTestRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = VscBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = VscBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    // Splashscreen
    implementation(libs.androidx.core.splashscreen)
    // Write trace events to the system trace buffer.
    implementation(libs.androidx.tracing.ktx)
    // LifeCycle
    implementation(libs.androidx.lifecycle.runtimeCompose)
    // WindowSizeClass
    implementation(libs.androidx.compose.material3.windowSizeClass)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // Timber
    implementation(libs.timber)

    // LeakCanary
    debugImplementation(libs.leakcanary)
    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.accompanist.testharness)
    testImplementation(libs.hilt.android.testing)

    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)

    androidTestImplementation(libs.accompanist.testharness)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.hilt.android.testing)


    // ====================== core:designsystem ===========================
    // Material Design 3
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material.iconsExtended)
    // main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.runtime)
    // Android Studio Preview support
    api(libs.androidx.compose.ui.tooling.preview)
    // Optional - Integration with window
    api(libs.androidx.window)
    // Optional - accompanist adaptive
    api(libs.accompanist.adaptive)
    // Coil
    api(libs.coil.kt.compose)
    api(libs.coil.kt.svg)

    debugApi(libs.androidx.compose.ui.tooling)

    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.accompanist.testharness)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)

    androidTestImplementation(libs.androidx.compose.ui.test)
}