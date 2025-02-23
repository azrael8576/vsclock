package com.wei.vsclock.feature.times

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewModelScope
import com.wei.vsclock.core.AppLocale
import com.wei.vsclock.core.base.BaseViewModel
import com.wei.vsclock.core.data.model.RefreshRate
import com.wei.vsclock.core.data.repository.RefreshStateRepository
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// 等 setApplicationLocales 套用後再更新狀態
private const val LANGUAGE_SWITCH_DELAY = 1_000L

@HiltViewModel
class TimesViewModel
@Inject constructor(
    @Dispatcher(VsclockDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val timeRepository: TimeRepository,
    private val refreshStateRepository: RefreshStateRepository,
) : BaseViewModel<
    TimesViewAction,
    TimesViewState,
    >(
    TimesViewState(),
) {
    // 用於進行 API 請求的協程作用域
    private var groupScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    // 用於自動刷新排程
    private var refreshJob: Job? = null
    private val firstLoad: MutableState<Boolean> = mutableStateOf(true)

    init {
        checkApiHealth()
        checkAppLanguage()
        observeSavedTimeZones()
        observeRefreshState()
    }

    private fun checkApiHealth() {
        viewModelScope.launch {
            timeRepository.checkHealth().asDataSourceResult().collect { result ->
                handleHealthResult(result)
            }
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

    private fun observeSavedTimeZones() {
        viewModelScope.launch {
            timeRepository.getCurrentTimes().collect { savedCurrentTimes ->
                updateState {
                    copy(
                        timesUiStateList = savedCurrentTimes.map {
                            it.toTimesUiState()
                        },
                    )
                }
                if (firstLoad.value) {
                    firstLoad.value = false
                    refreshCurrentTimes()
                }
            }
        }
    }

    private fun observeRefreshState() {
        viewModelScope.launch {
            refreshStateRepository.refreshRate.collect { rate ->
                updateState { copy(refreshRate = rate) }
            }
        }
    }

    /**
     * 載入時間：
     * 1. 取消舊的請求作用域並建立新的 groupScope
     * 2. 更新 Loading 狀態
     * 3. 呼叫 fetchTimes() 並更新 UI state，記錄刷新時間
     * 4. 根據當前刷新頻率排程下一次刷新
     */
    private fun refreshCurrentTimes() {
        val currentTimeMillis = System.currentTimeMillis()
        Timber.d("refreshCurrentTimes() ${Instant.fromEpochMilliseconds(currentTimeMillis)}")
        groupScope.cancel()
        groupScope = CoroutineScope(SupervisorJob() + ioDispatcher)

        groupScope.launch {
            updateState {
                copy(
                    timesLoadingState = TimesLoadingState.Loading,
                )
            }
            refreshStateRepository.updateLastRefreshTime(currentTimeMillis)
            scheduleNextRefresh()
            timeRepository.refreshCurrentTimes()
            updateState {
                copy(
                    timesLoadingState = TimesLoadingState.Finish,
                )
            }
        }
    }

    /**
     * 根據當前刷新頻率排程下一次自動刷新，
     * - `refreshJob` 在 `viewModelScope` 內運行，因此不會受到 `groupScope.cancel()` 的影響。
     * - 下一次刷新時間以 `refreshCurrentTimes()` 被觸發的時刻作為基準。
     * - 若有已存在的 `refreshJob`，則會先取消以確保新的排程生效。
     */
    private fun scheduleNextRefresh() {
        refreshJob?.cancel()
        val delayMillis = getDelayMillis(states.value.refreshRate)
        refreshJob = viewModelScope.launch(ioDispatcher) {
            delay(delayMillis)
            refreshCurrentTimes()
        }
    }

    /**
     * 根據刷新頻率取得延遲毫秒數。
     */
    private fun getDelayMillis(refreshRate: RefreshRate): Long {
        return TimeUnit.SECONDS.toMillis(refreshRate.second)
    }

    private fun onSwitchLanguage(appLocale: AppLocale) {
        val newLocales = LocaleListCompat.forLanguageTags(appLocale.code)
        AppCompatDelegate.setApplicationLocales(newLocales)

        viewModelScope.launch {
            delay(LANGUAGE_SWITCH_DELAY)
            updateState { copy(currentLanguage = appLocale) }
        }
    }

    /**
     * 當使用者選擇新的刷新頻率時，
     * 先更新狀態，再立即觸發 refreshCurrentTimes() 以取得最新資料，
     * 並利用新的頻率重新排程自動刷新。
     */
    private fun onSelectRefreshRate(refreshRate: RefreshRate) {
        refreshStateRepository.updateRefreshRate(refreshRate)
        refreshCurrentTimes()
    }

    private fun onClickTimeCard(timeZone: String) {
        viewModelScope.launch {
            refreshStateRepository.updateFloatingTime(timeZone)
            updateState { copy(isTimeCardClicked = true) }
        }
    }

    private fun onTimeCardClicked() {
        updateState { copy(isTimeCardClicked = false) }
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
            is TimesViewAction.ClickTimeCard -> onClickTimeCard(action.timeZone)
            TimesViewAction.TimeCardClicked -> onTimeCardClicked()
        }
    }

    /**
     * 當 ViewModel 被清理時釋放資源。
     */
    override fun onCleared() {
        super.onCleared()
        groupScope.cancel()
        refreshJob?.cancel()
    }
}
