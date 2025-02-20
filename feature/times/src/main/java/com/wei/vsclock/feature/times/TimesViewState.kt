package com.wei.vsclock.feature.times

import com.wei.vsclock.core.base.Action
import com.wei.vsclock.core.base.State

sealed class TimesViewAction : Action

data class TimesViewState(
    val apiHealthUiState: ApiHealthUiState = ApiHealthUiState.Loading,
) : State

sealed interface ApiHealthUiState {
    data class Success(val isHealth: Boolean) :
        ApiHealthUiState

    data object LoadFailed : ApiHealthUiState

    data object Loading : ApiHealthUiState
}
