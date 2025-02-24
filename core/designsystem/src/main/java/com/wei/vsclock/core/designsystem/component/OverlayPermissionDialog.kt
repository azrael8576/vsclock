package com.wei.vsclock.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wei.vsclock.core.designsystem.R
import com.wei.vsclock.core.designsystem.icon.VsclockIcons

@Composable
fun OverlayPermissionDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = VsclockIcons.Settings,
                contentDescription = null,
            )
        },
        title = {
            Text(text = stringResource(R.string.core_designsystem_overlay_permission_dialog_title))
        },
        text = {
            Text(
                text = stringResource(R.string.core_designsystem_overlay_permission_dialog_message),
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
                Text(stringResource(R.string.core_designsystem_overlay_permission_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(R.string.core_designsystem_overlay_permission_dialog_cancel))
            }
        },
    )
}
