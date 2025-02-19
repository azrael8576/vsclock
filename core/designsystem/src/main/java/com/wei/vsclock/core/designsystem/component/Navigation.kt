package com.wei.vsclock.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.designsystem.icon.VsclockIcons
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.VsclockTheme

/**
 * VsClock navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * @param modifier Modifier to be applied to this item.
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun RowScope.VsclockNavigationBarItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = VsclockNavigationDefaults.navigationContentColor(),
            selectedTextColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = VsclockNavigationDefaults.navigationContentColor(),
            indicatorColor = VsclockNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * VsClock navigation bar with content slot. Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun VsclockNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = VsclockNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Vsclock navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem].
 *
 * @param modifier Modifier to be applied to this item.
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun VsclockNavigationRailItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        enabled = enabled,
        label = null,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = VsclockNavigationDefaults.navigationContentColor(),
            selectedTextColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = VsclockNavigationDefaults.navigationContentColor(),
            indicatorColor = VsclockNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Vsclock navigation rail with header and content slots. Wraps Material 3 [NavigationRail].
 *
 * @param modifier Modifier to be applied to the navigation rail.
 * @param header Optional header that may hold a floating action button or a logo.
 * @param content Destinations inside the navigation rail. This should contain multiple
 * [NavigationRailItem]s.
 */
@Composable
fun VsclockNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = VsclockNavigationDefaults.navigationContentColor(),
        header = header,
        content = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                content()
            }
        },
    )
}

/**
 * Vsclock navigation drawer item with icon and label content slots. Wraps Material 3
 * [NavigationDrawerItem].
 *
 * @param modifier Modifier to be applied to this item.
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param selectedIcon The item icon content when selected.
 * @param label The item text label content.
 */
@Composable
fun VsclockNavigationDrawerItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable () -> Unit,
) {
    NavigationDrawerItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        label = {
            Box(modifier = Modifier.padding(horizontal = SPACING_LARGE.dp)) {
                label()
            }
        },
        colors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = VsclockNavigationDefaults.navigationContentColor(),
            selectedTextColor = VsclockNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = VsclockNavigationDefaults.navigationContentColor(),
        ),
    )
}

/**
 * Vsclock navigation drawer with content slot. Wraps Material 3 [PermanentDrawerSheet].
 *
 * @param modifier Modifier to be applied to the navigation drawer.
 * @param content Destinations inside the navigation drawer. This should contain multiple
 * [NavigationDrawerItem]s.
 */
@Composable
fun VsclockNavigationDrawer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    // TODO check on custom width of PermanentNavigationDrawer: b/232495216
    PermanentDrawerSheet(
        modifier = modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = Color.Transparent,
        drawerContentColor = VsclockNavigationDefaults.navigationContentColor(),
        content = {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                content()
            }
        },
    )
}

@ThemePreviews
@Composable
fun VsclockNavigationBarPreview() {
    VsclockTheme {
        VsclockNavigationBar {
            previewItems.forEachIndexed { index, item ->
                VsclockNavigationBarItem(
                    selected = index == 0,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = previewIcons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = previewSelectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun VsclockNavigationRailPreview() {
    VsclockTheme {
        VsclockBackground {
            VsclockNavigationRail {
                previewItems.forEachIndexed { index, item ->
                    VsclockNavigationRailItem(
                        selected = index == 0,
                        onClick = { },
                        icon = {
                            Icon(
                                imageVector = previewIcons[index],
                                contentDescription = item,
                            )
                        },
                        selectedIcon = {
                            Icon(
                                imageVector = previewSelectedIcons[index],
                                contentDescription = item,
                            )
                        },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun VsclockNavigationDrawerPreview() {
    VsclockTheme {
        VsclockBackground {
            VsclockNavigationDrawer {
                previewItems.forEachIndexed { index, item ->
                    VsclockNavigationDrawerItem(
                        selected = index == 0,
                        onClick = { },
                        icon = {
                            Icon(
                                imageVector = previewIcons[index],
                                contentDescription = item,
                            )
                        },
                        selectedIcon = {
                            Icon(
                                imageVector = previewSelectedIcons[index],
                                contentDescription = item,
                            )
                        },
                        label = { Text(item) },
                    )
                }
            }
        }
    }
}

/**
 * VsClock navigation default values.
 */
object VsclockNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

internal val previewItems = listOf("Times", "Settings")
internal val previewIcons = listOf(
    VsclockIcons.TimesBorder,
    VsclockIcons.SettingsBorder,
)
internal val previewSelectedIcons = listOf(
    VsclockIcons.Times,
    VsclockIcons.Settings,
)
