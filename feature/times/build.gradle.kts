plugins {
    alias(libs.plugins.vsclock.android.feature)
    alias(libs.plugins.vsclock.android.library.compose)
    alias(libs.plugins.vsclock.android.hilt)
}

android {
    namespace = "com.wei.vsclock.feature.times"
}

dependencies {
    implementation(projects.core.data)

    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.datetime)

    testImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
}