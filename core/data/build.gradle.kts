plugins {
    alias(libs.plugins.vsclock.android.library)
    alias(libs.plugins.vsclock.android.hilt)
}

android {
    namespace = "com.wei.vsclock.core.data"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.network)
    api(projects.core.model)
    api(projects.core.database)

    // Retrofit2
    implementation(libs.retrofit.core)
}
