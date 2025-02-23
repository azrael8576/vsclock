package com.wei.vsclock.feature.setting.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wei.vsclock.core.designsystem.component.ThemePreviews
import com.wei.vsclock.core.designsystem.theme.SPACING_EXTRA_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_LARGE
import com.wei.vsclock.core.designsystem.theme.VsclockTheme
import com.wei.vsclock.core.designsystem.theme.shapes

@Composable
internal fun AddTimeZoneDialog(
    availableTimeZones: List<String>,
    onClickTimeZone: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACING_LARGE.dp),
            shape = shapes.large,
        ) {
            LazyColumn {
                items(availableTimeZones) { availableTimeZone ->
                    Box(
                        modifier = Modifier.clickable {
                            onClickTimeZone(availableTimeZone)
                            onDismissRequest()
                        },
                    ) {
                        Text(
                            text = availableTimeZone,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(all = SPACING_EXTRA_LARGE.dp)
                                .fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun AddTimeZoneDialogPreview() {
    VsclockTheme {
        AddTimeZoneDialog(
            availableTimeZones = listOf(
                "FakeTimeZones - 1",
                "FakeTimeZones - 2",
                "FakeTimeZones - 3",
                "FakeTimeZones - 4",
                "FakeTimeZones - 5",
            ),
            onClickTimeZone = {},
            onDismissRequest = {},
        )
    }
}
