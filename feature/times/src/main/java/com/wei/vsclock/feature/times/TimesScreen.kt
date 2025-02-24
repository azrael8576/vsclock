package com.wei.vsclock.feature.times

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.data.model.RefreshRate
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.times.service.FloatingTimeService
import com.wei.vsclock.feature.times.ui.FloatingTimeConfirmationDialog
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
    launchPermissionRequest: () -> Unit,
) {
    val uiStates: TimesViewState by viewModel.states.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (uiStates.isTimeCardClicked) {
        viewModel.dispatch(
            TimesViewAction.TimeCardClicked,
        )
        context.startForegroundService(
            Intent(
                context,
                FloatingTimeService::class.java,
            ),
        )
        (context as? Activity)?.moveTaskToBack(true)
    }

    TimesScreen(
        uiStates = uiStates,
        launchPermissionRequest = launchPermissionRequest,
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
        onClickTimeCard = { timeZone ->
            viewModel.dispatch(
                TimesViewAction.ClickTimeCard(
                    timeZone,
                ),
            )
        },
    )
}

@Composable
internal fun TimesScreen(
    uiStates: TimesViewState,
    launchPermissionRequest: () -> Unit,
    onSwitchLanguage: (AppLocale) -> Unit,
    onSelectRefreshRate: (RefreshRate) -> Unit,
    onClickTimeCard: (String) -> Unit,
    withTopSpacer: Boolean = true,
    withBottomSpacer: Boolean = true,
    isPreview: Boolean = false,
) {
    val showSwitchLanguageDialog = remember { mutableStateOf(false) }
    val showFloatingTimeConfirmationDialog = remember { mutableStateOf(false) }
    val clickedTimeCardTimeZone = remember { mutableStateOf("") }
    val context = LocalContext.current

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

    if (showFloatingTimeConfirmationDialog.value) {
        FloatingTimeConfirmationDialog(
            onConfirmation = {
                if (!Settings.canDrawOverlays(context)) {
                    launchPermissionRequest()
                } else {
                    onClickTimeCard(clickedTimeCardTimeZone.value)
                }
                showFloatingTimeConfirmationDialog.value = false
            },
            onDismissRequest = {
                showFloatingTimeConfirmationDialog.value = false
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
                onClickSwitchLanguageButton = {
                    showSwitchLanguageDialog.value = true
                },
                onSelectRefreshRate = onSelectRefreshRate,
            )

            if (uiStates.timesLoadingState is TimesLoadingState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                // PlaceHolder for LinearProgressIndicator
                Spacer(modifier = Modifier.height(4.dp))
            }

            val timesUiStateList = uiStates.timesUiStateList
            if (timesUiStateList.isNotEmpty()) {
                TimesGrid(
                    timesUiStateList = uiStates.timesUiStateList,
                    onClickTimeCard = { timeZone ->
                        clickedTimeCardTimeZone.value = timeZone
                        showFloatingTimeConfirmationDialog.value = true
                    },
                )
            } else {
                NoDataMessage()
            }

            if (withBottomSpacer) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
    }
}

@Composable
fun NoDataMessage() {
    val noDataFound = stringResource(R.string.feature_times_no_data)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                contentDescription = noDataFound
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "(´･ω･`)",
                style = MaterialTheme.typography.displayMedium,
            )
            Spacer(modifier = Modifier.height(SPACING_SMALL.dp))
            Text(
                text = noDataFound,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@ThemePreviews
@Composable
fun TimesScreenPreview() {
    VsclockTheme {
        TimesScreen(
            uiStates = TimesViewState(),
            launchPermissionRequest = {},
            onSwitchLanguage = {},
            onSelectRefreshRate = {},
            onClickTimeCard = {},
            isPreview = true,
        )
    }
}
