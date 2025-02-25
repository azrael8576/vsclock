package com.wei.vsclock.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.window.layout.DisplayFeature
import com.wei.vsclock.R
import com.wei.vsclock.core.data.utils.NetworkMonitor
import com.wei.vsclock.core.designsystem.component.FunctionalityNotAvailablePopup
import com.wei.vsclock.core.designsystem.component.OverlayPermissionDialog
import com.wei.vsclock.core.designsystem.component.VsclockAppSnackbar
import com.wei.vsclock.core.designsystem.component.VsclockBackground
import com.wei.vsclock.core.designsystem.component.VsclockNavigationBar
import com.wei.vsclock.core.designsystem.component.VsclockNavigationBarItem
import com.wei.vsclock.core.designsystem.component.VsclockNavigationDrawer
import com.wei.vsclock.core.designsystem.component.VsclockNavigationDrawerItem
import com.wei.vsclock.core.designsystem.component.VsclockNavigationRail
import com.wei.vsclock.core.designsystem.component.VsclockNavigationRailItem
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.ui.VsclockNavigationType
import com.wei.vsclock.core.manager.ERROR_TEXT_PREFIX
import com.wei.vsclock.core.manager.Message
import com.wei.vsclock.core.manager.SnackbarManager
import com.wei.vsclock.core.manager.SnackbarState
import com.wei.vsclock.core.utils.UiText
import com.wei.vsclock.navigation.TopLevelDestination
import com.wei.vsclock.navigation.VsclockNavHost

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun VsclockApp(
    networkMonitor: NetworkMonitor,
    windowSizeClass: WindowSizeClass,
    displayFeatures: List<DisplayFeature>,
    overlayPermissionLauncher: ActivityResultLauncher<Intent>? = null,
    appState: VsclockAppState = rememberVsclockAppState(
        networkMonitor = networkMonitor,
        windowSizeClass = windowSizeClass,
        displayFeatures = displayFeatures,
    ),
    snackbarManager: SnackbarManager,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val vsclockBottomBar = stringResource(R.string.tag_vsclock_bottom_bar)
    val vsclockNavRail = stringResource(R.string.tag_vsclock_nav_rail)
    val vsclockNavDrawer = stringResource(R.string.tag_vsclock_nav_drawer)

    if (appState.showFunctionalityNotAvailablePopup.value) {
        FunctionalityNotAvailablePopup(
            onDismiss = {
                appState.showFunctionalityNotAvailablePopup.value = false
            },
        )
    }

    if (appState.showOverlayPermissionDialog.value) {
        OverlayPermissionDialog(
            onConfirmation = {
                overlayPermissionLauncher?.let {
                    launchOverlayPermissionSetting(context, it)
                }
                appState.showOverlayPermissionDialog.value = false
            },
            onDismissRequest = {
                appState.showOverlayPermissionDialog.value = false
            },
        )
    }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // If user is not connected to the internet show a snack bar to inform them.
    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarManager.showMessage(
                state = SnackbarState.Error,
                uiText = UiText.DynamicString(notConnectedMessage),
            )
        }
    }

    LaunchedEffect(key1 = snackbarHostState) {
        collectAndShowSnackbar(snackbarManager, snackbarHostState, context)
    }
    VsclockBackground {
        Scaffold(
            modifier = Modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        if (!appState.isFullScreenCurrentDestination) {
                            val isError = snackbarData.visuals.message.startsWith(ERROR_TEXT_PREFIX)
                            VsclockAppSnackbar(snackbarData, isError)
                        }
                    },
                )
            },
            bottomBar = {
                if (!appState.isFullScreenCurrentDestination &&
                    appState.navigationType == VsclockNavigationType.BOTTOM_NAVIGATION
                ) {
                    VsclockBottomBar(
                        destinations = appState.topLevelDestinations,
                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                        currentDestination = appState.currentDestination,
                        modifier = Modifier.testTag(vsclockBottomBar),
                    )
                }
            },
        ) { padding ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                if (!appState.isFullScreenCurrentDestination &&
                    appState.navigationType == VsclockNavigationType.PERMANENT_NAVIGATION_DRAWER
                ) {
                    VsclockNavDrawer(
                        destinations = appState.topLevelDestinations,
                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                        currentDestination = appState.currentDestination,
                        modifier = Modifier
                            .testTag(vsclockNavDrawer)
                            .padding(SPACING_LARGE.dp)
                            .safeDrawingPadding(),
                    )
                }

                if (!appState.isFullScreenCurrentDestination &&
                    appState.navigationType == VsclockNavigationType.NAVIGATION_RAIL
                ) {
                    VsclockNavRail(
                        destinations = appState.topLevelDestinations,
                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                        currentDestination = appState.currentDestination,
                        modifier = Modifier
                            .testTag(vsclockNavRail)
                            .safeDrawingPadding(),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    VsclockNavHost(
                        modifier = Modifier.fillMaxSize(),
                        appState = appState,
                        displayFeatures = displayFeatures,
                    )
                }
            }
        }
    }
}

@Composable
private fun VsclockNavDrawer(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    VsclockNavigationDrawer(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            VsclockNavigationDrawerItem(
                modifier = Modifier,
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId),
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId),
                    )
                },
            ) {
                Text(
                    text = stringResource(id = destination.iconTextId),
                )
            }
        }
    }
}

@Composable
private fun VsclockNavRail(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    VsclockNavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            VsclockNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(destination.iconTextId),
                    )
                },
                modifier = Modifier,
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(destination.iconTextId),
                    )
                },
            )
        }
    }
}

@Composable
private fun VsclockBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    VsclockNavigationBar(
        modifier = modifier,
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            VsclockNavigationBarItem(
                modifier = Modifier,
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId),
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId),
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

suspend fun collectAndShowSnackbar(
    snackbarManager: SnackbarManager,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    snackbarManager.messages.collect { messages ->
        if (messages.isNotEmpty()) {
            val message = messages.first()
            val text = getMessageText(message, context)

            if (message.state == SnackbarState.Error) {
                snackbarHostState.showSnackbar(
                    message = ERROR_TEXT_PREFIX + text,
                )
            } else {
                snackbarHostState.showSnackbar(message = text)
            }
            snackbarManager.setMessageShown(message.id)
        }
    }
}

fun getMessageText(message: Message, context: Context): String {
    return when (message.uiText) {
        is UiText.DynamicString -> (message.uiText as UiText.DynamicString).value
        is UiText.StringResource -> context.getString(
            (message.uiText as UiText.StringResource).resId,
            *(message.uiText as UiText.StringResource).args.map { it.toString(context) }
                .toTypedArray(),
        )
    }
}

private fun launchOverlayPermissionSetting(
    context: Context,
    overlayPermissionLauncher: ActivityResultLauncher<Intent>,
) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}"),
    )
    overlayPermissionLauncher.launch(intent)
}
