plugins {
    alias(libs.plugins.vsclock.android.library)
    alias(libs.plugins.vsclock.android.library.compose)
    alias(libs.plugins.vsclock.android.hilt)
}

android {
    namespace = "com.wei.vsclock.core.common"

    defaultConfig {
        testInstrumentationRunner = "com.wei.vsclock.core.testing.VsclockTestRunner"
    }
}

dependencies {
    // Kotlinx datetime
    api(libs.kotlinx.datetime)

    // LifeCycle
    implementation(libs.androidx.lifecycle.runtimeCompose)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(projects.core.testing)
    // For flow test
    testImplementation(libs.turbine)

    androidTestImplementation(projects.core.testing)
}
