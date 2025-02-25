import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.wei.vsclock.MainActivity
import kotlin.properties.ReadOnlyProperty

/**
 * Robot for [NavigationTest].
 *
 * 遵循此模型，找到測試使用者介面元素、檢查其屬性、和透過測試規則執行動作：
 * composeTestRule{.finder}{.assertion}{.action}
 *
 * Testing cheatsheet：
 * https://developer.android.com/jetpack/compose/testing-cheatsheet
 */
internal fun navigationRobot(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
    func: NavigationRobot.() -> Unit,
) = NavigationRobot(composeTestRule).apply(func)

internal open class NavigationRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>,
) {
    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
        ReadOnlyProperty<Any?, String> { _, _ -> activity.getString(resId) }

    // The strings used for matching in these tests
    private val times by composeTestRule.stringResource(com.wei.vsclock.R.string.times)
    private val setting by composeTestRule.stringResource(com.wei.vsclock.R.string.setting)

    private val navTimes by lazy {
        composeTestRule.onNodeWithContentDescription(
            times,
            useUnmergedTree = true,
        )
    }
    private val navSetting by lazy {
        composeTestRule.onNodeWithContentDescription(
            setting,
            useUnmergedTree = true,
        )
    }

    internal fun clickNavTimes() {
        navTimes.performClick()
        // 等待任何動畫完成
        composeTestRule.waitForIdle()
    }

    internal fun clickNavSetting() {
        navSetting.performClick()
        // 等待任何動畫完成
        composeTestRule.waitForIdle()
    }
}
