package com.wei.vsclock.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.wei.vsclock.core.designsystem.R

@Composable
fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            val functionalityNotAvailable = stringResource(R.string.core_designsystem_functionality_not_available)
            Text(
                text = functionalityNotAvailable,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics { contentDescription = functionalityNotAvailable },
            )
        },
        confirmButton = {
            val close = stringResource(R.string.core_designsystem_close)
            TextButton(onClick = onDismiss) {
                Text(
                    text = close,
                    modifier = Modifier.semantics { contentDescription = close },
                )
            }
        },
    )
}
