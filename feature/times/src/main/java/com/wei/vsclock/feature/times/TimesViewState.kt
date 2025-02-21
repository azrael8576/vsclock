package com.wei.vsclock.feature.times

import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.base.Action
import com.wei.vsclock.core.base.State

sealed class TimesViewAction : Action {
    data class SwitchLanguage(val appLocale: AppLocale) : TimesViewAction()
}

data class TimesViewState(
    val apiHealthUiState: ApiHealthUiState = ApiHealthUiState.Loading,
    val currentLanguage: AppLocale = AppLocale.EN,
) : State

sealed interface ApiHealthUiState {
    data class Success(val isHealth: Boolean) :
        ApiHealthUiState

    data object LoadFailed : ApiHealthUiState

    data object Loading : ApiHealthUiState
}
