package com.wei.vsclock.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/**
 * VsclockAppSnackbar 是一個 Composable function，根據是否為錯誤來顯示不同風格的 Snackbar。
 * @param snackbarData Snackbar的數據。
 * @param isError 是否為錯誤。
 * @param modifier 修改器，用於修改 Composable function 的屬性。
 * @param actionOnNewLine 是否將操作按鈕換行顯示。
 * @param shape Snackbar 的形狀。
 */
@Composable
fun VsclockAppSnackbar(
    snackbarData: SnackbarData,
    isError: Boolean,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
) {
    if (isError) {
        VsclockErrorSnackbar(
            snackbarData = snackbarData,
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
        )
    } else {
        VsclockSnackbar(
            snackbarData = snackbarData,
            modifier = modifier,
            actionOnNewLine = actionOnNewLine,
            shape = shape,
        )
    }
}

/**
 * VsclockErrorSnackbar 是一個 Composable function，用於顯示錯誤風格的 Snackbar。
 * @param snackbarData Snackbar的數據。
 * @param modifier 修改器，用於修改 Composable function 的屬性。
 * @param actionOnNewLine 是否將操作按鈕換行顯示。
 * @param shape Snackbar 的形狀。
 * @param backgroundColor Snackbar 的背景顏色。
 * @param contentColor Snackbar 的內容顏色。
 * @param actionColor Snackbar 的操作按鈕顏色。
 */
@Composable
private fun VsclockErrorSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
    contentColor: Color = MaterialTheme.colorScheme.onErrorContainer,
    actionColor: Color = MaterialTheme.colorScheme.onErrorContainer,
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = backgroundColor,
        contentColor = contentColor,
        actionColor = actionColor,
    )
}

/**
 * VsclockSnackbar 是一個 Composable function，用於顯示普通風格的 Snackbar。
 * @param snackbarData Snackbar的數據。
 * @param modifier 修改器，用於修改 Composable function 的屬性。
 * @param actionOnNewLine 是否將操作按鈕換行顯示。
 * @param shape Snackbar 的形狀。
 * @param backgroundColor Snackbar 的背景顏色。
 * @param contentColor Snackbar 的內容顏色。
 * @param actionColor Snackbar 的操作按鈕顏色。
 */
@Composable
private fun VsclockSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    actionColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = backgroundColor,
        contentColor = contentColor,
        actionColor = actionColor,
    )
}
