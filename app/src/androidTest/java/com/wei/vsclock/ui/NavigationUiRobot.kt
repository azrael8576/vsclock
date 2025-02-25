import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.DpSize
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.window.layout.FoldingFeature
import com.google.accompanist.testharness.TestHarness
import com.wei.vsclock.R
import com.wei.vsclock.core.data.utils.NetworkMonitor
import com.wei.vsclock.core.manager.SnackbarManager
import com.wei.vsclock.ui.VsclockApp
import com.wei.vsclock.uitesthiltmanifest.HiltComponentActivity
import com.wei.vsclock.utilities.FoldingDeviceUtil
import kotlin.properties.ReadOnlyProperty

/**
 * Robot for [NavigationUiTest].
 *
 * 遵循此模型，找到測試使用者介面元素、檢查其屬性、和透過測試規則執行動作：
 * composeTestRule{.finder}{.assertion}{.action}
 *
 * Testing cheatsheet：
 * https://developer.android.com/jetpack/compose/testing-cheatsheet
 */
internal fun navigationUiRobot(
    composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltComponentActivity>, HiltComponentActivity>,
    func: NavigationUiRobot.() -> Unit,
) = NavigationUiRobot(composeTestRule).apply(func)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
internal open class NavigationUiRobot(
    private val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<HiltComponentActivity>, HiltComponentActivity>,
) {
    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
        ReadOnlyProperty<Any?, String> { _, _ -> activity.getString(resId) }

    // The strings used for matching in these tests
    private val vsclockBottomBarTag by composeTestRule.stringResource(R.string.tag_vsclock_bottom_bar)
    private val vsclockNavRailTag by composeTestRule.stringResource(R.string.tag_vsclock_nav_rail)
    private val vsclockNavDrawerTag by composeTestRule.stringResource(R.string.tag_vsclock_nav_drawer)

    private val vsclockBottomBar by lazy {
        composeTestRule.onNodeWithTag(
            vsclockBottomBarTag,
            useUnmergedTree = true,
        )
    }

    private val vsclockNavRail by lazy {
        composeTestRule.onNodeWithTag(
            vsclockNavRailTag,
            useUnmergedTree = true,
        )
    }
    private val vsclockNavDrawer by lazy {
        composeTestRule.onNodeWithTag(
            vsclockNavDrawerTag,
            useUnmergedTree = true,
        )
    }

    fun setVsclockAppContent(
        dpSize: DpSize,
        networkMonitor: NetworkMonitor,
        snackbarManager: SnackbarManager,
        foldingState: FoldingFeature.State? = null,
        overlayPermissionLauncher: ActivityResultLauncher<Intent>? = null,
    ) {
        composeTestRule.setContent {
            TestHarness(dpSize) {
                BoxWithConstraints {
                    maxWidth
                    val displayFeatures = if (foldingState != null) {
                        val foldBounds = FoldingDeviceUtil.getFoldBounds(dpSize)
                        listOf(FoldingDeviceUtil.getFoldingFeature(foldBounds, foldingState))
                    } else {
                        emptyList()
                    }

                    VsclockApp(
                        windowSizeClass = WindowSizeClass.calculateFromSize(dpSize),
                        networkMonitor = networkMonitor,
                        displayFeatures = displayFeatures,
                        snackbarManager = snackbarManager,
                        overlayPermissionLauncher = overlayPermissionLauncher,
                    )
                }
            }
        }
    }

    fun setVsclockAppContentWithBookPosture(
        dpSize: DpSize,
        networkMonitor: NetworkMonitor,
        snackbarManager: SnackbarManager,
    ) {
        setVsclockAppContent(dpSize, networkMonitor, snackbarManager, FoldingFeature.State.HALF_OPENED)
    }

    fun verifyVsclockBottomBarDisplayed() {
        vsclockBottomBar.assertExists().assertIsDisplayed()
    }

    fun verifyVsclockNavRailDisplayed() {
        vsclockNavRail.assertExists().assertIsDisplayed()
    }

    fun verifyVsclockNavDrawerDisplayed() {
        vsclockNavDrawer.assertExists().assertIsDisplayed()
    }

    fun verifyVsclockBottomBarDoesNotExist() {
        vsclockBottomBar.assertDoesNotExist()
    }

    fun verifyVsclockNavRailDoesNotExist() {
        vsclockNavRail.assertDoesNotExist()
    }

    fun verifyVsclockNavDrawerDoesNotExist() {
        vsclockNavDrawer.assertDoesNotExist()
    }
}
