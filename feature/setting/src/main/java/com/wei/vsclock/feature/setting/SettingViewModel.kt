package com.wei.vsclock.feature.setting

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.wei.vsclock.core.base.BaseViewModel
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.manager.SnackbarManager
import com.wei.vsclock.core.manager.SnackbarState
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResultWithRetry
import com.wei.vsclock.core.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
@Inject
constructor(
    @Dispatcher(VsclockDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val timeRepository: TimeRepository,
    private val snackbarManager: SnackbarManager,
) : BaseViewModel<
    SettingViewAction,
    SettingViewState,
    >(
    SettingViewState(),
) {
    private var groupScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    init {
        getAvailableTimezones()
    }

    private fun getAvailableTimezones() {
        viewModelScope.launch {
            timeRepository.getAvailableTimeZones()
                .asDataSourceResultWithRetry(traceTag = "SettingViewModel.getAvailableTimezones()")
                .collect { result ->
                    handleAvailableTimeZonesResult(result)
                }
        }
    }

    private fun handleAvailableTimeZonesResult(result: DataSourceResult<List<String>>) {
        when (result) {
            DataSourceResult.Loading -> updateState {
                copy(
                    availableTimeZonesLoadingState = AvailableTimeZonesLoadingState.Loading,
                )
            }

            is DataSourceResult.Error -> updateState {
                copy(
                    availableTimeZonesLoadingState = AvailableTimeZonesLoadingState.Finish(
                        isSuccess = false,
                    ),
                )
            }

            is DataSourceResult.Success -> updateState {
                copy(
                    availableTimeZonesLoadingState = AvailableTimeZonesLoadingState.Finish(
                        isSuccess = true,
                    ),
                    availableTimeZones = result.data,
                )
            }
        }
    }

    private fun showSnackBar(
        snackbarState: SnackbarState = SnackbarState.Default,
        @StringRes resId: Int? = null,
        message: List<String>,
    ) {
        if (resId == null) {
            snackbarManager.showMessage(
                state = snackbarState,
                uiText = UiText.DynamicString(message.first()),
            )
        } else {
            snackbarManager.showMessage(
                state = snackbarState,
                uiText =
                UiText.StringResource(
                    resId,
                    message.map {
                        UiText.StringResource.Args.DynamicString(it)
                    }.toList(),
                ),
            )
        }
    }

    private fun onClickAddTimeZone(timeZone: String) {
        Timber.e("onClickAddTimeZone timeZone $timeZone")
    }

    private fun onClickEditButton() {
        updateState { copy(headerUiMode = HeaderUiMode.EDIT) }
    }

    private fun onClickDeleteButton() {
        updateState { copy(headerUiMode = HeaderUiMode.DEFAULT) }
    }

    /**
     * 處理用戶的 UI 操作，例如點擊一個按鈕。具體的實現將根據操作來更新狀態或發送事件。
     *
     * 通過 dispatch 統一進行事件的分發，有利於 View 與 ViewModel 間進一步解偶，
     * 同時也方便進行日誌分析與後續處理。
     *
     * @param action 用戶的 UI 操作。
     */
    override fun dispatch(action: SettingViewAction) {
        Timber.d("dispatch $action")
        when (action) {
            is SettingViewAction.ShowSnackBar -> showSnackBar(
                resId = action.resId,
                message = action.message,
            )
            is SettingViewAction.ClickAddTimeZone -> onClickAddTimeZone(action.timeZone)
            SettingViewAction.ClickEditButton -> onClickEditButton()
            SettingViewAction.ClickDeleteButton -> onClickDeleteButton()
        }
    }

    /**
     * 當 ViewModel 被清理時釋放資源。
     */
    override fun onCleared() {
        super.onCleared()
        groupScope.cancel()
    }
}
