package com.wei.vsclock.ui.robot

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.wei.vsclock.MainActivity
import kotlin.properties.ReadOnlyProperty
import com.wei.vsclock.feature.times.R as FeatureTimesR

/**
 * Screen Robot for End To End Test.
 *
 * 遵循此模型，找到測試使用者介面元素、檢查其屬性、和透過測試規則執行動作：
 * composeTestRule{.finder}{.assertion}{.action}
 *
 * Testing cheatsheet：
 * https://developer.android.com/jetpack/compose/testing-cheatsheet
 */
internal fun timesEndToEndRobot(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    func: TimesEndToEndRobot.() -> Unit,
) = TimesEndToEndRobot(composeTestRule).apply(func)

internal open class TimesEndToEndRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {
    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
        ReadOnlyProperty<Any?, String> { _, _ -> activity.getString(resId) }

    // The strings used for matching in these tests
    private val refreshRateDescription by composeTestRule.stringResource(FeatureTimesR.string.feature_times_refresh_rate)

    private val refreshRate by lazy {
        composeTestRule.onNode(hasContentDescription(refreshRateDescription))
    }

    fun verifyRefreshRateDisplayed() {
        refreshRate.assertExists().assertIsDisplayed()
    }
}
