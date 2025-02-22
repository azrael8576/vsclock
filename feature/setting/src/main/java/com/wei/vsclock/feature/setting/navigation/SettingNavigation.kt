package com.wei.vsclock.feature.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.wei.vsclock.feature.setting.SettingRoute

const val SETTING_ROUTE = "setting_route"
const val RESET_HEADER = "resetHeader"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    this.currentBackStackEntry?.savedStateHandle?.set(RESET_HEADER, true)
    this.navigate(SETTING_ROUTE, navOptions)
}

fun NavGraphBuilder.settingGraph(
    navController: NavController,
) {
    composable(route = SETTING_ROUTE) {
        SettingRoute(
            navController = navController,
        )
    }
}
