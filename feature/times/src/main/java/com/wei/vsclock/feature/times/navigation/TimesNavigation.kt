package com.wei.vsclock.feature.times.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.wei.vsclock.feature.times.TimesRoute

const val TIMES_ROUTE = "times_route"

fun NavController.navigateToTimes(navOptions: NavOptions? = null) {
    this.navigate(TIMES_ROUTE, navOptions)
}

fun NavGraphBuilder.timesGraph(
    navController: NavController,
) {
    composable(route = TIMES_ROUTE) {
        TimesRoute(
            navController = navController,
        )
    }
}
