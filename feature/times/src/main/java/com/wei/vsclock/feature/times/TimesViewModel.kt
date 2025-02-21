package com.wei.vsclock.feature.times

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.base.BaseViewModel
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResult
import com.wei.vsclock.core.result.asDataSourceResultWithRetry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TimesViewModel
@Inject
constructor(
    @Dispatcher(VsclockDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val timeRepository: TimeRepository,
) : BaseViewModel<
    TimesViewAction,
    TimesViewState,
    >(
    TimesViewState(),
) {
    private var groupScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    init {
        checkApiHealth()
        checkAppLanguage()
        loadTimes()
    }

    private fun checkApiHealth() {
        viewModelScope.launch {
            timeRepository.checkHealth()
                .asDataSourceResult()
                .collect { result ->
                    handleHealthResult(result)
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

    private fun loadTimes() {
        groupScope.cancel()
        groupScope = CoroutineScope(SupervisorJob() + ioDispatcher)

        groupScope.launch {
            updateState { copy(timesLoadingState = TimesLoadingState.Loading) }

            // 利用 async 進行併發呼叫，最後透過 awaitAll 一次取得所有結果
            val timesUiStateList: List<TimesUiState> = fetchTimes()

            updateState {
                copy(
                    timesLoadingState = TimesLoadingState.Finish,
                    timesUiStateList = timesUiStateList,
                )
            }
        }
    }

    private suspend fun fetchTimes(): List<TimesUiState> =
        coroutineScope {
            fakeTimeZones.map { zone ->
                async {
                    // 取出第一個非 Idle 的結果
                    val result = timeRepository.getCurrentTime(zone)
                        .asDataSourceResultWithRetry(
                            maxRetries = 3,
                            traceTag = "timeRepository.getCurrentTime: $zone",
                        )
                        .first { it !is DataSourceResult.Loading }
                    result.toTimesUiState(zone = zone)
                }
            }.awaitAll()
        }

    private fun DataSourceResult<CurrentTime>.toTimesUiState(zone: String): TimesUiState {
        // 將不同結果轉換成對應的 UI state
        return when (this) {
            is DataSourceResult.Success -> data.toTimesUiState(isSuccess = true)
            is DataSourceResult.Error -> TimesUiState(isSuccess = false, time = "", timeZone = zone)
            else -> TimesUiState(isSuccess = false, time = "", timeZone = zone)
        }
    }

    private fun handleHealthResult(result: DataSourceResult<Int>) {
        updateState {
            copy(
                healthLoadingState = when (result) {
                    is DataSourceResult.Success -> HealthLoadingState.Finish(
                        isSuccess = true,
                        isHealth = result.data == 200,
                    )

                    is DataSourceResult.Error -> HealthLoadingState.Finish(
                        isSuccess = false,
                        isHealth = false,
                    )

                    is DataSourceResult.Loading -> HealthLoadingState.Loading
                },
            )
        }
    }

    private fun onSwitchLanguage(appLocale: AppLocale) {
        val newLocales = LocaleListCompat.forLanguageTags(appLocale.code)
        AppCompatDelegate.setApplicationLocales(newLocales)

        viewModelScope.launch {
            // 等 setApplicationLocales 套用後再更新狀態
            delay(1_000L)
            updateState { copy(currentLanguage = appLocale) }
        }
    }

    private fun onSelectRefreshRate(refreshRate: RefreshRate) {
        updateState { copy(refreshRate = refreshRate) }
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
            is TimesViewAction.SelectRefreshRate -> onSelectRefreshRate(action.refreshRate)
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

/**
 * TODO: Test Data
 */
val fakeTimeZones = listOf(
    "Africa/Abidjan",
    "America/Mexico_City",
    "Asia/Taipei",
    "Asia/Omsk",
    "Asia/Seoul",
    "Asia/Tokyo",
    "Japan",
    "US/Central",
)
