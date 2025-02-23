package com.wei.vsclock.feature.times.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wei.vsclock.core.designsystem.theme.SPACING_EXTRA_LARGE
import com.wei.vsclock.core.designsystem.theme.SPACING_SMALL
import com.wei.vsclock.core.designsystem.theme.shapes
import com.wei.vsclock.feature.times.TimesUiState

@Composable
internal fun TimesGrid(
    timesUiStateList: List<TimesUiState>,
    onClickTimeCard: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = SPACING_EXTRA_LARGE.dp),
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(SPACING_EXTRA_LARGE.dp),
        verticalArrangement = Arrangement.spacedBy(SPACING_EXTRA_LARGE.dp),
    ) {
        items(timesUiStateList.size) { index ->
            val timesUiState = timesUiStateList[index]
            TimeCard(
                modifier = Modifier.clickable {
                    onClickTimeCard(timesUiState.timeZone)
                },
                timesUiState = timesUiStateList[index],
            )
        }
    }
}

@Composable
internal fun TimeCard(
    modifier: Modifier = Modifier,
    timesUiState: TimesUiState,
) {
    Card(
        modifier = modifier,
        shape = shapes.extraSmall,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = SPACING_EXTRA_LARGE.dp,
                    horizontal = SPACING_SMALL.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = timesUiState.time,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = timesUiState.timeZone,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
