package com.wei.vsclock.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.wei.vsclock.R
import com.wei.vsclock.core.designsystem.icon.VsclockIcons

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    TIMES(
        selectedIcon = VsclockIcons.Times,
        unselectedIcon = VsclockIcons.TimesBorder,
        iconTextId = R.string.times,
        titleTextId = R.string.times,
    ),
    SETTING(
        selectedIcon = VsclockIcons.Settings,
        unselectedIcon = VsclockIcons.SettingsBorder,
        iconTextId = R.string.setting,
        titleTextId = R.string.setting,
    ),
}
