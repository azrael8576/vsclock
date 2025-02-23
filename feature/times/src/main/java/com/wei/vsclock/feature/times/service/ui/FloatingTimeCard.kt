package com.wei.vsclock.feature.times.service.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.feature.times.R
import com.wei.vsclock.feature.times.TimesUiState
import com.wei.vsclock.feature.times.ui.TimeCard

@Composable
internal fun FloatingTimeCard(
    currentTime: CurrentTime?,
    onClick: () -> Unit,
    onDrag: (Float, Float) -> Unit,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (currentTime != null) {
            TimeCard(
                modifier = Modifier.width(170.dp),
                timesUiState = TimesUiState(
                    time = currentTime.time,
                    timeZone = currentTime.timeZone,
                ),
            )
        } else {
            NoDataMessage()
        }
    }
}

@Composable
private fun NoDataMessage() {
    val somethingWrong =
        stringResource(R.string.feature_times_oops_something_went_wrong_please_try_again)
    Card(
        modifier = Modifier
            .width(170.dp)
            .semantics {
                contentDescription = somethingWrong
            },

    ) {
        Column(
            modifier = Modifier.padding(all = SPACING_SMALL.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Floating Time",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(SPACING_SMALL.dp))
            Text(
                text = somethingWrong,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
