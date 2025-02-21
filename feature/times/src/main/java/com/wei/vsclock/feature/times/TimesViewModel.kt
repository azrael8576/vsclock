package com.wei.vsclock.feature.times

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.base.BaseViewModel
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimesViewModel
@Inject
constructor(
    private val timeRepository: TimeRepository,
) : BaseViewModel<
    TimesViewAction,
    TimesViewState,
    >(
    TimesViewState(),
) {

    init {
        checkApiHealth()
        checkAppLanguage()
    }

    private fun checkApiHealth() {
        viewModelScope.launch {
            timeRepository.checkHealth()
                .asDataSourceResult()
                .collect { result ->
                    handleApiHealthResult(result)
                }
        }
    }

    private fun checkAppLanguage() {
        updateState {
            copy(
                currentLanguage = when (
                    AppCompatDelegate.getApplicationLocales()
                        .toLanguageTags()
                ) {
                    AppLocale.EN.code -> AppLocale.EN
                    AppLocale.ZH_HANT_TW.code -> AppLocale.ZH_HANT_TW
                    else -> AppLocale.EN
                },
            )
        }
    }

    private fun handleApiHealthResult(result: DataSourceResult<Int>) {
        when (result) {
            is DataSourceResult.Success -> handleApiHealthSuccess(result.data)
            is DataSourceResult.Error -> handleApiHealthError()
            is DataSourceResult.Loading -> handleLoading()
        }
    }

    private fun handleApiHealthSuccess(statusCode: Int) {
        updateState {
            copy(
                apiHealthUiState = ApiHealthUiState.Success(isHealth = statusCode == 200),
            )
        }
    }

    private fun handleApiHealthError() {
        updateState { copy(apiHealthUiState = ApiHealthUiState.LoadFailed) }
    }

    private fun handleLoading() {
        updateState { copy(apiHealthUiState = ApiHealthUiState.Loading) }
    }

    private fun onSwitchLanguage(appLocale: AppLocale) {
        val newLocales = LocaleListCompat.forLanguageTags(appLocale.code)
        AppCompatDelegate.setApplicationLocales(newLocales)

        viewModelScope.launch {
            // 等 setApplicationLocales 套用後再更新狀態
            delay(1_000L)
            updateState {
                copy(currentLanguage = appLocale)
            }
        }
    }

    /**
     * 處理用戶的 UI 操作，例如點擊一個按鈕。具體的實現將根據操作來更新狀態或發送事件。
     *
     * 通過 dispatch 統一進行事件的分發，有利於 View 與 ViewModel 間進一步解偶，
     * 同時也方便進行日誌分析與後續處理。
     *
     * @param action 用戶的 UI 操作。
     */
    override fun dispatch(action: TimesViewAction) {
        Timber.d("dispatch $action")
        when (action) {
            is TimesViewAction.SwitchLanguage -> onSwitchLanguage(action.appLocale)
        }
    }
}
