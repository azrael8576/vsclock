package com.wei.vsclock.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import com.wei.vsclock.MainActivity
import com.wei.vsclock.ui.robot.timesEndToEndRobot
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import navigationRobot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Tests all the navigation flows that are handled by the navigation library.
 */
@HiltAndroidTest
class NavigationTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Create a temporary folder used to create a Data Store file. This guarantees that
     * the file is removed in between each test, preventing a crash.
     */
    @BindValue
    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    /**
     * Use the primary activity to initialize the app normally.
     */
    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() = hiltRule.inject()

    @Test
    fun firstScreen_isTimes() {
        timesEndToEndRobot(composeTestRule) {
            verifyRefreshRateDisplayed()
        }
    }

    /*
     * When pressing back from any top level destination except "Times", the app navigates back
     * to the "Times" destination, no matter which destinations you visited in between.
     */
    @Test
    fun navigationBar_backFromAnyDestination_returnsToTimes() {
        timesEndToEndRobot(composeTestRule) {
            navigationRobot(composeTestRule) {
                // GIVEN the user is on any of the top level destinations, THEN the Up arrow is not shown.
                clickNavSetting()
                // WHEN the user uses the system button/gesture to go back
                Espresso.pressBack()
            }
            verifyRefreshRateDisplayed()
        }
    }

    /*
     * There should always be at most one instance of a top-level destination at the same time.
     */
    @Test(expected = NoActivityResumedException::class)
    fun topDestination_back_quitsApp() {
        timesEndToEndRobot(composeTestRule) {
            navigationRobot(composeTestRule) {
                // GIVEN the user navigates to the Setting destination
                clickNavSetting()
                // and then navigates to the Times destination
                clickNavTimes()
                // WHEN the user uses the system button/gesture to go back
                Espresso.pressBack()
                // THEN the app quits
            }
        }
    }
}
