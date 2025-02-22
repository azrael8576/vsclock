package com.wei.vsclock.feature.setting

import androidx.annotation.StringRes
import com.wei.vsclock.core.base.Action
import com.wei.vsclock.core.base.State

sealed class SettingViewAction : Action {
    data class ShowSnackBar(
        @StringRes val resId: Int? = null,
        val message: List<String>,
    ) : SettingViewAction()

    data class ClickAddTimeZone(val timeZone: String) : SettingViewAction()
    data object ClickEditButton : SettingViewAction()
    data object ClickDeleteButton : SettingViewAction()
}

data class SettingViewState(
    val headerUiMode: HeaderUiMode = HeaderUiMode.DEFAULT,
    val availableTimeZonesLoadingState: AvailableTimeZonesLoadingState = AvailableTimeZonesLoadingState.Idle,
    val availableTimeZones: List<String> = emptyList(),
) : State

enum class HeaderUiMode {
    DEFAULT,
    EDIT,
}

sealed interface AvailableTimeZonesLoadingState {
    data object Idle : AvailableTimeZonesLoadingState
    data object Loading : AvailableTimeZonesLoadingState
    data class Finish(
        val isSuccess: Boolean,
    ) : AvailableTimeZonesLoadingState
}
