package com.wei.vsclock.feature.times

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.designsystem.component.FunctionalityNotAvailablePopup
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.times.ui.SwitchLanguageDialog

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
    )
}

@Composable
internal fun TimesScreen(
    uiStates: TimesViewState,
    onSwitchLanguage: (AppLocale) -> Unit,
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

    val showSwitchLanguagePopup = remember { mutableStateOf(false) }

    if (showSwitchLanguagePopup.value) {
        SwitchLanguageDialog(
            onDismissRequest = {
                showSwitchLanguagePopup.value = false
            },
            currentLocale = uiStates.currentLanguage,
            onConfirmation = { selectedLocale ->
                showSwitchLanguagePopup.value = false
                if (selectedLocale == uiStates.currentLanguage) return@SwitchLanguageDialog
                onSwitchLanguage(selectedLocale)
            },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            if (withTopSpacer) {
                Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SPACING_LARGE.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SwitchLanguageButton(
                    language = uiStates.currentLanguage,
                    onClick = { showSwitchLanguagePopup.value = true },
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Screen not available \uD83D\uDE48",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .semantics { contentDescription = "" },
                )

                val apiHealth = uiStates.apiHealthUiState
                if (apiHealth is ApiHealthUiState.Success) {
                    val isHealth = apiHealth.isHealth

                    Text(
                        text = "api health: $isHealth",
                        color = if (isHealth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            if (withBottomSpacer) {
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
            }
        }
    }
}

@Composable
internal fun SwitchLanguageButton(
    language: AppLocale,
    onClick: () -> Unit,
) {
    val text = stringResource(R.string.feature_times_language) + ": ${language.text}"
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(top = SPACING_SMALL.dp)
            .semantics { contentDescription = text },
    ) {
        Text(
            text = text,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        )
    }
}

@ThemePreviews
@Composable
fun TimesScreenPreview() {
    VsclockTheme {
        TimesScreen(
            uiStates = TimesViewState(),
            onSwitchLanguage = {},
            isPreview = true,
        )
    }
}
