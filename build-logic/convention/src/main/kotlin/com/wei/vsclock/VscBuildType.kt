package com.wei.vsclock

/**
 * This is shared between :app module to provide configurations type safety.
 */
enum class VscBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE
}
