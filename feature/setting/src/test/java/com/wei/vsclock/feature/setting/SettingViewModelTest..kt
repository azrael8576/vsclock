package com.wei.vsclock.feature.setting

import com.google.common.truth.Truth.assertThat
import com.wei.vsclock.core.manager.SnackbarManager
import com.wei.vsclock.core.testing.data.currentTimeTestData
import com.wei.vsclock.core.testing.repository.TestTimeRepository
import com.wei.vsclock.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

/**
 * Unit tests for [SettingViewModel].
 * 在此測試只關注 SettingViewModelTest 的職責。
 *
 * Test Case 命名原則：
 * fun $操作 should $預期行為 when $條件`() { ... }
 *
 * 遵循此模型，安排、操作、斷言：
 * {Arrange}{Act}{Assert}
 */
class SettingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val timeRepository = TestTimeRepository()

    private lateinit var viewModel: SettingViewModel
    private lateinit var snackbarManager: SnackbarManager

    @Before
    fun setup() {
        snackbarManager = SnackbarManager()
        viewModel = SettingViewModel(timeRepository, snackbarManager)
    }

    @Test
    fun `initSettingViewModel should update correct availableTimeZones when time zones are provided by repository`() =
        runTest {
            // Arrange
            val testTimeZones = listOf("UTC", "Asia/Taipei")
            timeRepository.setAvailableTimeZones(testTimeZones)

            // Act
            advanceUntilIdle()

            // Assert
            val state = viewModel.states.value
            assertThat(state.availableTimeZones).isEqualTo(testTimeZones)
            assertThat(state.availableTimeZonesLoadingState).isInstanceOf(
                AvailableTimeZonesLoadingState.Finish::class.java,
            )
            val finishState =
                state.availableTimeZonesLoadingState as AvailableTimeZonesLoadingState.Finish
            assertThat(finishState.isSuccess).isTrue()
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `initSettingViewModel should update correct savedTimeZones`() =
        runTest {
            // Arrange,
            timeRepository.setCurrentTimes(
                listOf(
                    currentTimeTestData,
                ),
            )

            // Act
            advanceUntilIdle()

            // Assert
            assertThat(viewModel.states.value.savedTimeZones).isEqualTo(listOf(currentTimeTestData.timeZone))
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `dispatch ResetHeaderUiMode action should update headerUiMode to DEFAULT and clear selectedTimeZones`() =
        runTest {
            // Arrange,
            // @Before default UIState

            // Act
            viewModel.dispatch(SettingViewAction.ResetHeaderUiMode)

            // Assert
            assertThat(viewModel.states.value.headerUiMode).isEqualTo(HeaderUiMode.DEFAULT)
            assertThat(viewModel.states.value.selectedTimeZones).isEmpty()
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `dispatch ClickAddTimeZone action should show snackbar when adding a duplicate time zone`() =
        runTest {
            // Arrange
            viewModel.updateState {
                copy(
                    headerUiMode = headerUiMode,
                    availableTimeZonesLoadingState = availableTimeZonesLoadingState,
                    availableTimeZones = availableTimeZones,
                    savedTimeZones = listOf(currentTimeTestData.timeZone),
                    selectedTimeZones = selectedTimeZones,
                )
            }

            // Act
            viewModel.dispatch(SettingViewAction.ClickAddTimeZone(currentTimeTestData.timeZone))
            advanceUntilIdle()

            // Assert
            assertThat(snackbarManager.messages.value).isNotEmpty()
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `dispatch ClickAddTimeZone action not should show snackbar when time zone does not exist yet`() =
        runTest {
            // Arrange,
            // @Before default UIState

            // Act
            viewModel.dispatch(SettingViewAction.ClickAddTimeZone(currentTimeTestData.timeZone))
            advanceUntilIdle()

            // Assert
            assertThat(snackbarManager.messages.value).isEmpty()
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `dispatch onClickEditTimeZone action should show snackbar when exist a duplicate time zone`() =
        runTest {
            // Arrange
            viewModel.updateState {
                copy(
                    headerUiMode = headerUiMode,
                    availableTimeZonesLoadingState = availableTimeZonesLoadingState,
                    availableTimeZones = availableTimeZones,
                    savedTimeZones = listOf(currentTimeTestData.timeZone),
                    selectedTimeZones = selectedTimeZones,
                )
            }

            // Act
            viewModel.dispatch(
                SettingViewAction.ClickEditTimeZone(
                    "previousTimeZone",
                    currentTimeTestData.timeZone,
                ),
            )
            advanceUntilIdle()

            // Assert
            assertThat(snackbarManager.messages.value).isNotEmpty()
        }

    @Suppress("ktlint:standard:max-line-length")
    @Test
    fun `dispatch onClickEditTimeZone action not should show snackbar when time zone does not exist yet`() =
        runTest {
            // Arrange,
            val previousTimeZone = "previousTimeZone"
            viewModel.updateState {
                copy(
                    headerUiMode = HeaderUiMode.DEFAULT,
                    availableTimeZonesLoadingState = availableTimeZonesLoadingState,
                    availableTimeZones = listOf(),
                    savedTimeZones = listOf(previousTimeZone),
                    selectedTimeZones = setOf(),
                )
            }

            // Act
            viewModel.dispatch(
                SettingViewAction.ClickEditTimeZone(
                    previousTimeZone,
                    currentTimeTestData.timeZone,
                ),
            )
            advanceUntilIdle()

            // Assert
            assertThat(snackbarManager.messages.value).isEmpty()
        }

    // TODO Wei: other test...
}
