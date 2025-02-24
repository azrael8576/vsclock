package com.wei.vsclock.feature.times.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wei.vsclock.feature.times.R

@Composable
fun FloatingTimeConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.feature_times_dialog_floating_time_title))
        },
        text = {
            Text(
                text = stringResource(R.string.feature_times_dialog_floating_time_message),
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text(stringResource(R.string.feature_times_dialog_floating_time_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(R.string.feature_times_dialog_floating_time_cancel))
            }
        },
    )
}
