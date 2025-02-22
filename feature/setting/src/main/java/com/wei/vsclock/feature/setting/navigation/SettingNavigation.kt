package com.wei.vsclock.feature.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.wei.vsclock.feature.setting.SettingRoute

const val SETTING_ROUTE = "setting_route"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
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
