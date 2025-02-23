package com.wei.vsclock.feature.setting.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.feature.setting.HeaderUiMode
import com.wei.vsclock.feature.setting.R
import com.wei.vsclock.feature.setting.SettingViewState

private data class ActionButtonData(val text: String, val onClick: () -> Unit)

@Composable
internal fun SettingHeader(
    uiStates: SettingViewState,
    onClickAddButton: () -> Unit,
    onClickEditButton: () -> Unit,
    onClickDeleteButton: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SPACING_LARGE.dp),
        horizontalAlignment = Alignment.End,
    ) {
        HeaderRow(
            when (uiStates.headerUiMode) {
                HeaderUiMode.DEFAULT -> listOf(
                    ActionButtonData(
                        stringResource(R.string.feature_setting_add),
                        onClickAddButton,
                    ),
                    ActionButtonData(
                        stringResource(R.string.feature_setting_edit),
                        onClickEditButton,
                    ),
                )

                HeaderUiMode.EDIT -> listOf(
                    ActionButtonData(
                        stringResource(R.string.feature_setting_delete),
                        onClickDeleteButton,
                    ),
                )
            },
        )
    }
}

@Composable
private fun HeaderRow(buttons: List<ActionButtonData>) {
    Row(horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp)) {
        buttons.forEach { button ->
            ActionButton(button)
        }
    }
}

@Composable
private fun ActionButton(buttonData: ActionButtonData) {
    Button(
        onClick = buttonData.onClick,
        modifier = Modifier
            .semantics { contentDescription = buttonData.text }
            .padding(top = SPACING_SMALL.dp),
    ) {
        Text(
            text = buttonData.text,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        )
    }
}
