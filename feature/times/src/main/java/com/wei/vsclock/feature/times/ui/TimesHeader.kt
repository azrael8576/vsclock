package com.wei.vsclock.feature.times.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.feature.times.HealthLoadingState
import com.wei.vsclock.feature.times.R
import com.wei.vsclock.feature.times.RefreshRate
import com.wei.vsclock.feature.times.TimesViewState

@Composable
internal fun TimesHeader(
    uiStates: TimesViewState,
    onClickSwitchLanguageButton: () -> Unit,
    onSelectRefreshRate: (RefreshRate) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SPACING_LARGE.dp),
        horizontalAlignment = Alignment.End,
    ) {
        SwitchLanguageButton(
            language = uiStates.currentLanguage,
            onClick = onClickSwitchLanguageButton,
        )
        RefreshRateInfoRow(
            selectedRefreshRate = uiStates.refreshRate,
            onSelectRefreshRate = onSelectRefreshRate,
        )
    }
}

@Composable
private fun SwitchLanguageButton(
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

@Composable
private fun RefreshRateInfoRow(
    selectedRefreshRate: RefreshRate,
    onSelectRefreshRate: (RefreshRate) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(top = SPACING_SMALL.dp),
        horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.feature_times_refresh_rate),
            style = MaterialTheme.typography.bodyLarge,
        )
        SingleChoiceSegmentedButton(
            selectedRefreshRate = selectedRefreshRate,
            onSelectRefreshRate = onSelectRefreshRate,
        )
        Text(
            text = stringResource(R.string.feature_times_min),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleChoiceSegmentedButton(
    selectedRefreshRate: RefreshRate,
    onSelectRefreshRate: (RefreshRate) -> Unit,
) {
    val options = RefreshRate.entries

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, _ ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                ),
                onClick = { onSelectRefreshRate(options[index]) },
                selected = options[index] == selectedRefreshRate,
                label = { Text(options[index].min.toString()) },
            )
        }
    }
}

@ThemePreviews
@Composable
fun RefreshRateInfoRowPreview() {
    VsclockTheme {
        Surface {
            TimesHeader(
                uiStates = TimesViewState(
                    healthLoadingState = HealthLoadingState.Idle,
                    currentLanguage = AppLocale.EN,
                    refreshRate = RefreshRate.MIN_1,
                    timesUiStateList = listOf(),
                ),
                onClickSwitchLanguageButton = {},
                onSelectRefreshRate = {},
            )
        }
    }
}
