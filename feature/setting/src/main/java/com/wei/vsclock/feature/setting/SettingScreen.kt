package com.wei.vsclock.feature.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.setting.ui.AddTimeZoneDialog
import com.wei.vsclock.feature.setting.ui.SettingHeader

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
 *
 */
@Composable
internal fun SettingRoute(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val uiStates: SettingViewState by viewModel.states.collectAsStateWithLifecycle()

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
        onClickEditButton = { viewModel.dispatch(SettingViewAction.ClickEditButton) },
        onClickDeleteButton = { viewModel.dispatch(SettingViewAction.ClickDeleteButton) },
    )
}

@Composable
internal fun SettingScreen(
    uiStates: SettingViewState,
    onShowSnackBar: (Int?, String) -> Unit,
    onClickAddTimeZone: (String) -> Unit,
    onClickEditButton: () -> Unit,
    onClickDeleteButton: () -> Unit,
    withTopSpacer: Boolean = true,
    withBottomSpacer: Boolean = true,
    isPreview: Boolean = false,
) {
    val showAddLanguageDialog = remember { mutableStateOf(false) }

    if (showAddLanguageDialog.value) {
        AddTimeZoneDialog(
            availableTimeZones = uiStates.availableTimeZones,
            onClickTimeZone = { timeZone ->
                onClickAddTimeZone(timeZone)
            },
            onDismissRequest = {
                showAddLanguageDialog.value = false
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
                    showAddLanguageDialog.value = true
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
            isPreview = true,
            onShowSnackBar = { _, _ -> },
            onClickAddTimeZone = { _ -> },
            onClickEditButton = { },
            onClickDeleteButton = { },
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
            isPreview = true,
            onShowSnackBar = { _, _ -> },
            onClickAddTimeZone = { _ -> },
            onClickEditButton = { },
            onClickDeleteButton = { },
        )
    }
}
