plugins {
    alias(libs.plugins.vsclock.android.feature)
    alias(libs.plugins.vsclock.android.library.compose)
    alias(libs.plugins.vsclock.android.hilt)
}

android {
    namespace = "com.wei.vsclock.feature.setting"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.appcompat)

    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
}