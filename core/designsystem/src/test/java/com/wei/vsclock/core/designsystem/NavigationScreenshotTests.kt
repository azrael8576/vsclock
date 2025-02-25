package com.wei.vsclock.core.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.accompanist.testharness.TestHarness
import com.wei.vsclock.core.designsystem.component.VsclockNavigationBar
import com.wei.vsclock.core.designsystem.component.VsclockNavigationBarItem
import com.wei.vsclock.core.designsystem.component.VsclockNavigationDrawer
import com.wei.vsclock.core.designsystem.component.VsclockNavigationDrawerItem
import com.wei.vsclock.core.designsystem.component.VsclockNavigationRail
import com.wei.vsclock.core.designsystem.component.VsclockNavigationRailItem
import com.wei.vsclock.core.designsystem.icon.VsclockIcons
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.core.testing.util.DefaultRoborazziOptions
import com.wei.vsclock.core.testing.util.captureMultiTheme
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, sdk = [33], qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class NavigationScreenshotTests() {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun navigationBar_multipleThemes() {
        composeTestRule.captureMultiTheme("NavigationBar") {
            Surface {
                VsclockNavigationBarExample()
            }
        }
    }

    @Test
    fun navigationBar_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                TestHarness(fontScale = 2f) {
                    VsclockTheme {
                        VsclockNavigationBarExample("Looong item")
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                "src/test/screenshots/NavigationBar" +
                    "/NavigationBar_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    @Test
    fun navigationRail_multipleThemes() {
        composeTestRule.captureMultiTheme("NavigationRail") {
            Surface {
                VsclockNavigationRailExample()
            }
        }
    }

    @Test
    fun navigationDrawer_multipleThemes() {
        composeTestRule.captureMultiTheme("NavigationDrawer") {
            Surface {
                VsclockNavigationDrawerExample()
            }
        }
    }

    @Test
    fun navigationDrawer_hugeFont() {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
            ) {
                TestHarness(fontScale = 2f) {
                    VsclockTheme {
                        VsclockNavigationDrawerExample("Loooooooooooooooong item")
                    }
                }
            }
        }
        composeTestRule.onRoot()
            .captureRoboImage(
                "src/test/screenshots/NavigationDrawer" +
                    "/NavigationDrawer_fontScale2.png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }

    @Composable
    private fun VsclockNavigationBarExample(label: String = "Item") {
        VsclockNavigationBar {
            (0..2).forEach { index ->
                VsclockNavigationBarItem(
                    selected = index == 0,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = VsclockIcons.UpcomingBorder,
                            contentDescription = "",
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = VsclockIcons.Upcoming,
                            contentDescription = "",
                        )
                    },
                    label = { Text(label) },
                )
            }
        }
    }

    @Composable
    private fun VsclockNavigationRailExample() {
        VsclockNavigationRail {
            (0..2).forEach { index ->
                VsclockNavigationRailItem(
                    selected = index == 0,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = VsclockIcons.UpcomingBorder,
                            contentDescription = "",
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = VsclockIcons.Upcoming,
                            contentDescription = "",
                        )
                    },
                )
            }
        }
    }

    @Composable
    private fun VsclockNavigationDrawerExample(label: String = "Item") {
        VsclockNavigationDrawer {
            (0..2).forEach { index ->
                VsclockNavigationDrawerItem(
                    selected = index == 0,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = VsclockIcons.UpcomingBorder,
                            contentDescription = "",
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = VsclockIcons.Upcoming,
                            contentDescription = "",
                        )
                    },
                    label = { Text(label) },
                )
            }
        }
    }
}
