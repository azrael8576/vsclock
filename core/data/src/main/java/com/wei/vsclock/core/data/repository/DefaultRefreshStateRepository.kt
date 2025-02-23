package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.data.model.RefreshRate
import com.wei.vsclock.core.database.dao.CurrentTimeDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * `DefaultRefreshStateRepository` 負責管理應用程式的自動刷新狀態，並提供最新時間資訊給 UI 層使用。
 *
 * 這個 Repository 透過 `@Singleton` 設定，確保所有使用它的元件（例如 `TimesScreen` 和 `FloatingTimeCard`）
 * 都能夠共用同一組數據，確保時間狀態的同步與一致性。
 */
@Singleton
class DefaultRefreshStateRepository
@Inject
constructor() : RefreshStateRepository {

    private val _lastRefreshTime = MutableStateFlow(0L)
    override val lastRefreshTime: Flow<Long> = _lastRefreshTime.asStateFlow()

    private val _refreshRate = MutableStateFlow(RefreshRate.MIN_1)
    override val refreshRate: Flow<RefreshRate> = _refreshRate.asStateFlow()

    private val _currentFloatingTime = MutableStateFlow("")
    override val currentFloatingTime: Flow<String> = _currentFloatingTime.asStateFlow()

    override fun updateLastRefreshTime(timeMillis: Long) {
        _lastRefreshTime.value = timeMillis
    }

    override fun updateRefreshRate(rate: RefreshRate) {
        _refreshRate.value = rate
    }

    override fun updateFloatingTime(timeZone: String) {
        _currentFloatingTime.value = timeZone
    }
}
