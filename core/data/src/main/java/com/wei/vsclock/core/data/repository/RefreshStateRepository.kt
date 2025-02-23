package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.data.model.RefreshRate
import kotlinx.coroutines.flow.Flow

interface RefreshStateRepository {
    val lastRefreshTime: Flow<Long>
    val refreshRate: Flow<RefreshRate>
    val currentFloatingTime: Flow<String>

    fun updateLastRefreshTime(timeMillis: Long)
    fun updateRefreshRate(rate: RefreshRate)
    fun updateFloatingTime(timeZone: String)
}
