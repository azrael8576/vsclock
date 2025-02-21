package com.wei.vsclock.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.window.layout.DisplayFeature
import com.wei.vsclock.core.designsystem.ui.DeviceOrientation
import com.wei.vsclock.feature.times.navigation.TIMES_ROUTE
import com.wei.vsclock.feature.times.navigation.timesGraph
import com.wei.vsclock.ui.VsclockAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun VsclockNavHost(
    modifier: Modifier = Modifier,
    appState: VsclockAppState,
    displayFeatures: List<DisplayFeature>,
    startDestination: String = TIMES_ROUTE,
) {
    val navController = appState.navController
    val navigationType = appState.navigationType
    val isPortrait = appState.currentDeviceOrientation == DeviceOrientation.PORTRAIT
    val contentType = appState.contentType

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        timesGraph(
            navController = navController,
        )
    }
}
