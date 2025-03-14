package com.wei.vsclock.feature.times

import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.base.Action
import com.wei.vsclock.core.base.State
import com.wei.vsclock.core.data.model.RefreshRate
import com.wei.vsclock.core.model.data.CurrentTime

sealed class TimesViewAction : Action {
    data class SwitchLanguage(val appLocale: AppLocale) : TimesViewAction()
    data class SelectRefreshRate(val refreshRate: RefreshRate) : TimesViewAction()
    data class ClickTimeCard(val timeZone: String) : TimesViewAction()
    data object TimeCardClicked : TimesViewAction()
}

data class TimesViewState(
    val healthLoadingState: HealthLoadingState = HealthLoadingState.Idle,
    val currentLanguage: AppLocale = AppLocale.EN,
    val refreshRate: RefreshRate = RefreshRate.MIN_1,
    val timesLoadingState: TimesLoadingState = TimesLoadingState.Idle,
    val timesUiStateList: List<TimesUiState> = emptyList(),
    val isTimeCardClicked: Boolean = false,
) : State

sealed interface HealthLoadingState {
    data object Idle : HealthLoadingState
    data object Loading : HealthLoadingState
    data class Finish(
        val isSuccess: Boolean,
        val isHealth: Boolean,
    ) : HealthLoadingState
}

sealed interface TimesLoadingState {
    data object Idle : TimesLoadingState
    data object Loading : TimesLoadingState
    data object Finish : TimesLoadingState
}

data class TimesUiState(
    val time: String,
    val timeZone: String,
) : State

fun CurrentTime.toTimesUiState(): TimesUiState =
    TimesUiState(
        time = this.time.ifEmpty { "--:--" },
        timeZone = this.timeZone,
    )
