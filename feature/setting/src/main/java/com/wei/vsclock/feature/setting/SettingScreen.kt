package com.wei.vsclock.feature.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_MEDIUM
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.setting.navigation.RESET_HEADER
import com.wei.vsclock.feature.setting.ui.AddTimeZoneDialog
import com.wei.vsclock.feature.setting.ui.EditTimeZoneDialog
import com.wei.vsclock.feature.setting.ui.SettingHeader
import com.wei.vsclock.feature.setting.ui.TimeZoneCard

/**
 *
 * UI 事件決策樹
 * 下圖顯示了一個決策樹，用於查找處理特定事件用例的最佳方法。
 *
 *                                                      ┌───────┐
 *                                                      │ Start │
 *                                                      └───┬───┘
 *                                                          ↓
 *                                       ┌───────────────────────────────────┐
 *                                       │ Where is event originated?        │
 *                                       └──────┬─────────────────────┬──────┘
 *                                              ↓                     ↓
 *                                              UI                  ViewModel
 *                                              │                     │
 *                           ┌─────────────────────────┐      ┌───────────────┐
 *                           │ When the event requires │      │ Update the UI │
 *                           │ ...                     │      │ State         │
 *                           └─┬─────────────────────┬─┘      └───────────────┘
 *                             ↓                     ↓
 *                        Business logic      UI behavior logic
 *                             │                     │
 *     ┌─────────────────────────────────┐   ┌──────────────────────────────────────┐
 *     │ Delegate the business logic to  │   │ Modify the UI element state in the   │
 *     │ the ViewModel                   │   │ UI directly                          │
 *     └─────────────────────────────────┘   └──────────────────────────────────────┘
 *
 * 設定畫面的入口函數，負責監聽 `NavController` 的 `savedStateHandle`，並在使用者透過
 * `navigateToSetting()` 進入此畫面時，自動重置 `headerUiMode` 為 `HeaderUiMode.DEFAULT`。
 *
 * ## 為什麼不在 `ViewModel` 內直接監聽 `SavedStateHandle`？
 * 這個方法主要參考了以下的討論：
 * [How can I collect, observe changes from savedStateHandle in a ViewModel?](https://slack-chats.kotlinlang.org/t/16932540/how-can-i-collect-observe-changes-from-savedstatehandle-in-a)
 *
 */
@Composable
internal fun SettingRoute(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val uiStates: SettingViewState by viewModel.states.collectAsStateWithLifecycle()
    // 監聽 NavController BackStackEntry 的 savedStateHandle
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(key1 = savedStateHandle) {
        val shouldReset = savedStateHandle?.get<Boolean>(RESET_HEADER) ?: false
        if (shouldReset) {
            viewModel.dispatch(SettingViewAction.ResetHeaderUiMode)
            savedStateHandle?.remove<Boolean>(RESET_HEADER)
        }
    }

    SettingScreen(
        uiStates = uiStates,
        onShowSnackBar = { resId, message ->
            viewModel.dispatch(
                SettingViewAction.ShowSnackBar(
                    resId = resId,
                    message = listOf(message),
                ),
            )
        },
        onClickAddTimeZone = { timeZone ->
            viewModel.dispatch(
                SettingViewAction.ClickAddTimeZone(
                    timeZone,
                ),
            )
        },
        onClickEditTimeZone = { previousTimeZone, updatedTimeZone ->
            viewModel.dispatch(
                SettingViewAction.ClickEditTimeZone(
                    previousTimeZone,
                    updatedTimeZone,
                ),
            )
        },
        onClickEditButton = { viewModel.dispatch(SettingViewAction.ClickEditButton) },
        onClickDeleteButton = { viewModel.dispatch(SettingViewAction.ClickDeleteButton) },
        onSelectTimeZoneCheckBox = { timeZone ->
            viewModel.dispatch(
                SettingViewAction.SelectTimeZoneCheckBox(
                    timeZone,
                ),
            )
        },
        onDeselectTimeZoneCheckBox = { timeZone ->
            viewModel.dispatch(
                SettingViewAction.DeselectTimeZoneCheckBox(
                    timeZone,
                ),
            )
        },
    )
}

@Composable
internal fun SettingScreen(
    uiStates: SettingViewState,
    onShowSnackBar: (Int?, String) -> Unit,
    onClickAddTimeZone: (String) -> Unit,
    onClickEditTimeZone: (String, String) -> Unit,
    onClickEditButton: () -> Unit,
    onClickDeleteButton: () -> Unit,
    onSelectTimeZoneCheckBox: (String) -> Unit,
    onDeselectTimeZoneCheckBox: (String) -> Unit,
    withTopSpacer: Boolean = true,
    withBottomSpacer: Boolean = true,
    isPreview: Boolean = false,
) {
    val showAddTimeZoneDialog = remember { mutableStateOf(false) }

    if (showAddTimeZoneDialog.value) {
        AddTimeZoneDialog(
            availableTimeZones = uiStates.availableTimeZones,
            onClickTimeZone = { timeZone ->
                onClickAddTimeZone(timeZone)
            },
            onDismissRequest = {
                showAddTimeZoneDialog.value = false
            },
        )
    }
    val selectedTimeZoneForEdit = remember { mutableStateOf("") }
    val showEditTimeZoneDialog = remember { mutableStateOf(false) }

    if (showEditTimeZoneDialog.value) {
        EditTimeZoneDialog(
            availableTimeZones = uiStates.availableTimeZones,
            onClickTimeZone = { updatedTimeZone ->
                onClickEditTimeZone(selectedTimeZoneForEdit.value, updatedTimeZone)
            },
            onDismissRequest = {
                selectedTimeZoneForEdit.value = ""
                showEditTimeZoneDialog.value = false
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (withTopSpacer) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }

            SettingHeader(
                uiStates = uiStates,
                onClickAddButton = {
                    if (uiStates.availableTimeZones.isEmpty()) {
                        onShowSnackBar(null, "Failed to find any IANA time zones.")
                        return@SettingHeader
                    }
                    showAddTimeZoneDialog.value = true
                },
                onClickEditButton = onClickEditButton,
                onClickDeleteButton = onClickDeleteButton,
            )

            if (uiStates.availableTimeZonesLoadingState is AvailableTimeZonesLoadingState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                // PlaceHolder for LinearProgressIndicator
                Spacer(modifier = Modifier.height(4.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SPACING_MEDIUM.dp),
                contentPadding = PaddingValues(horizontal = SPACING_LARGE.dp),
            ) {
                itemsIndexed(uiStates.savedTimeZones) { _, timeZone ->
                    val headerUiMode = uiStates.headerUiMode
                    TimeZoneCard(
                        timeZone = timeZone,
                        isEditMode = headerUiMode == HeaderUiMode.EDIT,
                        isChecked = uiStates.selectedTimeZones.contains(timeZone),
                        onClick = {
                            if (headerUiMode == HeaderUiMode.EDIT) {
                                if (uiStates.availableTimeZones.isEmpty()) {
                                    onShowSnackBar(null, "Failed to find any IANA time zones.")
                                    return@TimeZoneCard
                                }
                                selectedTimeZoneForEdit.value = timeZone
                                showEditTimeZoneDialog.value = true
                            }
                        },
                        onSelectTimeZoneCheckBox = onSelectTimeZoneCheckBox,
                        onDeselectTimeZoneCheckBox = onDeselectTimeZoneCheckBox,
                    )
                }
            }

            if (withBottomSpacer) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
    }
}

@ThemePreviews
@Composable
fun SettingScreenWithDefaultHeaderPreview() {
    VsclockTheme {
        SettingScreen(
            uiStates = SettingViewState(
                headerUiMode = HeaderUiMode.DEFAULT,
            ),
            onShowSnackBar = { _, _ -> },
            onClickAddTimeZone = { _ -> },
            onClickEditTimeZone = { _, _ -> },
            onClickEditButton = { },
            onClickDeleteButton = { },
            isPreview = true,
            onSelectTimeZoneCheckBox = { _ -> },
            onDeselectTimeZoneCheckBox = { _ -> },
        )
    }
}

@ThemePreviews
@Composable
fun SettingScreenWithEditHeaderPreview() {
    VsclockTheme {
        SettingScreen(
            uiStates = SettingViewState(
                headerUiMode = HeaderUiMode.EDIT,
            ),
            onShowSnackBar = { _, _ -> },
            onClickAddTimeZone = { _ -> },
            onClickEditTimeZone = { _, _ -> },
            onClickEditButton = { },
            onClickDeleteButton = { },
            isPreview = true,
            onSelectTimeZoneCheckBox = { _ -> },
            onDeselectTimeZoneCheckBox = { _ -> },
        )
    }
}
