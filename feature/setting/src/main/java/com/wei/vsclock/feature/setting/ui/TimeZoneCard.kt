package com.wei.vsclock.feature.setting.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.designsystem.theme.SPACING_MEDIUM
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL

@Composable
internal fun TimeZoneCard(
    timeZone: String,
    isEditMode: Boolean,
    isChecked: Boolean,
    onClick: () -> Unit,
    onSelectTimeZoneCheckBox: (String) -> Unit,
    onDeselectTimeZoneCheckBox: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        Row(
            modifier = Modifier.padding(all = SPACING_MEDIUM.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SPACING_SMALL.dp),
        ) {
            val checkboxSize = 24.dp
            if (isEditMode) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = {
                        if (!isChecked) {
                            onSelectTimeZoneCheckBox(timeZone)
                        } else {
                            onDeselectTimeZoneCheckBox(timeZone)
                        }
                    },
                    modifier = Modifier.size(checkboxSize),
                )
            } else {
                Spacer(modifier = Modifier.height(checkboxSize))
            }
            Text(
                text = timeZone,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
