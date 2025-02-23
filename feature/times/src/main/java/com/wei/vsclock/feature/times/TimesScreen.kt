package com.wei.vsclock.feature.times

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
import androidx.compose.material3.Text
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
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.designsystem.component.FunctionalityNotAvailablePopup
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.times.ui.SwitchLanguageDialog
import com.wei.vsclock.feature.times.ui.TimesGrid
import com.wei.vsclock.feature.times.ui.TimesHeader

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
internal fun TimesRoute(
    navController: NavController,
    viewModel: TimesViewModel = hiltViewModel(),
) {
    val uiStates: TimesViewState by viewModel.states.collectAsStateWithLifecycle()

    TimesScreen(
        uiStates = uiStates,
        onSwitchLanguage = { appLocale ->
            viewModel.dispatch(
                TimesViewAction.SwitchLanguage(
                    appLocale,
                ),
            )
        },
        onSelectRefreshRate = { refreshRate ->
            viewModel.dispatch(
                TimesViewAction.SelectRefreshRate(
                    refreshRate,
                ),
            )
        },
    )
}

@Composable
internal fun TimesScreen(
    uiStates: TimesViewState,
    onSwitchLanguage: (AppLocale) -> Unit,
    onSelectRefreshRate: (RefreshRate) -> Unit,
    withTopSpacer: Boolean = true,
    withBottomSpacer: Boolean = true,
    isPreview: Boolean = false,
) {
    val showPopup = remember { mutableStateOf(false) }

    if (showPopup.value) {
        FunctionalityNotAvailablePopup(
            onDismiss = {
                showPopup.value = false
            },
        )
    }

    val showSwitchLanguageDialog = remember { mutableStateOf(false) }

    if (showSwitchLanguageDialog.value) {
        SwitchLanguageDialog(
            onDismissRequest = {
                showSwitchLanguageDialog.value = false
            },
            currentLocale = uiStates.currentLanguage,
            onConfirmation = { selectedLocale ->
                showSwitchLanguageDialog.value = false
                if (selectedLocale == uiStates.currentLanguage) return@SwitchLanguageDialog
                onSwitchLanguage(selectedLocale)
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

            TimesHeader(
                uiStates = uiStates,
                onClickSwitchLanguageButton = { showSwitchLanguageDialog.value = true },
                onSelectRefreshRate = onSelectRefreshRate,
            )

            if (uiStates.timesLoadingState is TimesLoadingState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                // PlaceHolder for LinearProgressIndicator
                Spacer(modifier = Modifier.height(4.dp))
                // TODO Wei: 待 [TimesViewModel] 移除 fakeTimeZones 移除此 UI
                Text(
                    text = "The data is fake.",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            TimesGrid(uiStates.timesUiStateList)

            if (withBottomSpacer) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
    }
}

@ThemePreviews
@Composable
fun TimesScreenPreview() {
    VsclockTheme {
        TimesScreen(
            uiStates = TimesViewState(),
            onSwitchLanguage = {},
            onSelectRefreshRate = {},
            isPreview = true,
        )
    }
}
