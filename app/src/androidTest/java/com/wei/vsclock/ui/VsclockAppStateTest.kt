package com.wei.vsclock.ui

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.DpSize
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.window.layout.FoldingFeature
import com.google.common.truth.Truth.assertThat
import com.wei.vsclock.core.designsystem.ui.VsclockContentType
import com.wei.vsclock.core.designsystem.ui.VsclockNavigationType
import com.wei.vsclock.core.testing.util.TestNetworkMonitor
import com.wei.vsclock.utilities.COMPACT_HEIGHT
import com.wei.vsclock.utilities.COMPACT_WIDTH
import com.wei.vsclock.utilities.EXPANDED_HEIGHT
import com.wei.vsclock.utilities.EXPANDED_WIDTH
import com.wei.vsclock.utilities.FoldingDeviceUtil
import com.wei.vsclock.utilities.MEDIUM_HEIGHT
import com.wei.vsclock.utilities.MEDIUM_WIDTH
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Tests [VsclockAppState].
 *
 * Note: This could become an unit test if Robolectric is added to the project and the Context
 * is faked.
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class VsclockAppStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Create the test dependencies.
    private val networkMonitor = TestNetworkMonitor()

    // Subject under test.
    private lateinit var state: VsclockAppState

    @Test
    fun verifyCurrentDestinationIsSet_whenNavigatedToDestination() = runTest {
        var currentDestination: String? = null

        composeTestRule.setContent {
            val navController = rememberTestNavController()
            state = remember(navController) {
                VsclockAppState(
                    navController = navController,
                    coroutineScope = backgroundScope,
                    windowSizeClass = getCompactWindowClass(),
                    networkMonitor = networkMonitor,
                    displayFeatures = emptyList(),
                )
            }

            // Update currentDestination whenever it changes
            currentDestination = state.currentDestination?.route

            // Navigate to destination b once
            LaunchedEffect(Unit) {
                navController.setCurrentDestination("b")
            }
        }

        assertThat("b").isEqualTo(currentDestination)
    }

    @Test
    fun verifyTopLevelDestinationsContainExpectedNames() = runTest {
        composeTestRule.setContent {
            state = rememberVsclockAppState(
                windowSizeClass = getCompactWindowClass(),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
        }

        assertThat(state.topLevelDestinations).hasSize(2)
        assertThat(state.topLevelDestinations[0].name).ignoringCase().contains("times")
        assertThat(state.topLevelDestinations[1].name).ignoringCase().contains("setting")
    }

    @Test
    fun verifyBottomNavigationDisplayed_whenWindowSizeIsCompact() = runTest {
        composeTestRule.setContent {
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = getCompactWindowClass(),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.navigationType).isEqualTo(VsclockNavigationType.BOTTOM_NAVIGATION)
        }
    }

    @Test
    fun verifyNavigationRailDisplayed_whenWindowSizeIsMedium() = runTest {
        composeTestRule.setContent {
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    DpSize(
                        MEDIUM_WIDTH,
                        MEDIUM_HEIGHT,
                    ),
                ),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.navigationType).isEqualTo(VsclockNavigationType.NAVIGATION_RAIL)
        }
    }

    @Test
    fun verifyPermanentNavDrawerDisplayed_whenWindowSizeIsExpanded() = runTest {
        composeTestRule.setContent {
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    DpSize(
                        EXPANDED_WIDTH,
                        EXPANDED_HEIGHT,
                    ),
                ),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.navigationType).isEqualTo(VsclockNavigationType.PERMANENT_NAVIGATION_DRAWER)
        }
    }

    @Test
    fun verifyContentTypeIsSINGLE_PANE_whenWindowSizeIsCompact() = runTest {
        composeTestRule.setContent {
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = getCompactWindowClass(),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.contentType).isEqualTo(VsclockContentType.SINGLE_PANE)
        }
    }

    @Test
    fun verifyContentTypeIsSINGLE_PANE_whenWindowSizeIsMedium_withNormalPosture() = runTest {
        composeTestRule.setContent {
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(
                    DpSize(
                        MEDIUM_WIDTH,
                        MEDIUM_HEIGHT,
                    ),
                ),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.contentType).isEqualTo(VsclockContentType.SINGLE_PANE)
        }
    }

    @Test
    fun verifyContentTypeIsDUAL_PANE_whenWindowSizeIsMedium_withBookPosture() = runTest {
        composeTestRule.setContent {
            val dpSize = DpSize(MEDIUM_WIDTH, MEDIUM_HEIGHT)
            val foldBounds = FoldingDeviceUtil.getFoldBounds(dpSize)
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(dpSize),
                networkMonitor = networkMonitor,
                displayFeatures = listOf(
                    FoldingDeviceUtil.getFoldingFeature(
                        foldBounds,
                        FoldingFeature.State.HALF_OPENED,
                    ),
                ),
            )
            assertThat(state.contentType).isEqualTo(VsclockContentType.DUAL_PANE)
        }
    }

    @Test
    fun verifyContentTypeIsDUAL_PANE_whenWindowSizeIsMedium_withSeparating() = runTest {
        composeTestRule.setContent {
            val dpSize = DpSize(MEDIUM_WIDTH, MEDIUM_HEIGHT)
            val foldBounds = FoldingDeviceUtil.getFoldBounds(dpSize)
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(dpSize),
                networkMonitor = networkMonitor,
                displayFeatures = listOf(
                    FoldingDeviceUtil.getFoldingFeature(
                        foldBounds,
                        FoldingFeature.State.FLAT,
                    ),
                ),
            )
            assertThat(state.contentType).isEqualTo(VsclockContentType.DUAL_PANE)
        }
    }

    @Test
    fun verifyContentTypeIsDUAL_PANE_whenWindowSizeIsExpanded() = runTest {
        composeTestRule.setContent {
            val dpSize = DpSize(EXPANDED_WIDTH, EXPANDED_HEIGHT)
            state = VsclockAppState(
                navController = NavHostController(LocalContext.current),
                coroutineScope = backgroundScope,
                windowSizeClass = WindowSizeClass.calculateFromSize(dpSize),
                networkMonitor = networkMonitor,
                displayFeatures = emptyList(),
            )
            assertThat(state.contentType).isEqualTo(VsclockContentType.DUAL_PANE)
        }
    }

    @Test
    fun verifyStateIsOffline_whenNetworkMonitorReportsDisconnection() =
        runTest(UnconfinedTestDispatcher()) {
            val results = mutableListOf<Boolean>()

            composeTestRule.setContent {
                state = VsclockAppState(
                    navController = NavHostController(LocalContext.current),
                    coroutineScope = backgroundScope,
                    windowSizeClass = WindowSizeClass.calculateFromSize(
                        DpSize(
                            EXPANDED_WIDTH,
                            EXPANDED_HEIGHT,
                        ),
                    ),
                    networkMonitor = networkMonitor,
                    displayFeatures = emptyList(),
                )
            }

            backgroundScope.launch {
                state.isOffline.collect { value ->
                    results.add(value)
                }
            }

            networkMonitor.setConnected(false)
            assertTrue(results.contains(true))
        }

    private fun getCompactWindowClass() =
        WindowSizeClass.calculateFromSize(DpSize(COMPACT_WIDTH, COMPACT_HEIGHT))
}

@Composable
private fun rememberTestNavController(): TestNavHostController {
    val context = LocalContext.current
    return remember<TestNavHostController> {
        TestNavHostController(context).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = "a") {
                composable("a") { }
                composable("b") { }
            }
        }
    }
}
