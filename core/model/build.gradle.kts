plugins {
    alias(libs.plugins.vsclock.android.library)
    alias(libs.plugins.vsclock.android.library.compose)
    alias(libs.plugins.vsclock.android.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.wei.vsclock.core.model"
}

dependencies {
    // For androidx.compose.runtime.Stable
    implementation(libs.androidx.compose.runtime)

    implementation(libs.kotlinx.datetime)
}