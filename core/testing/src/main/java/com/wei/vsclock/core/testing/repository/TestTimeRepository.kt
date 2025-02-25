package com.wei.vsclock.core.testing.repository

import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.testing.data.currentTimeTestData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

// TODO Wei
class TestTimeRepository : TimeRepository {

    // 利用 MutableStateFlow 作為回傳資料的管道，預設值可以設定成空或初始資料
    private val availableTimeZonesFlow = MutableStateFlow<List<String>>(emptyList())
    private val currentTimesFlow = MutableStateFlow<List<CurrentTime>>(emptyList())
    private val currentTimeFlow = MutableStateFlow<CurrentTime?>(null)

    /**
     * A test-only API to allow controlling the list of availableTimeZonesFlow from tests.
     */
    fun setAvailableTimeZones(timeZones: List<String>) {
        availableTimeZonesFlow.value = timeZones
    }

    /**
     * A test-only API to allow controlling the list of currentTimesFlow from tests.
     */
    fun setCurrentTimes(times: List<CurrentTime>) {
        currentTimesFlow.value = times
    }

    override suspend fun checkHealth(): Flow<Int> = flow { emit(200) }

    override suspend fun refreshCurrentTimes() {
        // 不需要實作，或者根據需求模擬行為
    }

    override suspend fun refreshCurrentTime(timeZone: String) {
        // 模擬更新行為
    }

    override suspend fun getAvailableTimeZones(): Flow<List<String>> =
        availableTimeZonesFlow

    override suspend fun getCurrentTimes(): Flow<List<CurrentTime>> =
        currentTimesFlow

    override suspend fun getCurrentTime(timeZone: String): Flow<CurrentTime?> =
        currentTimeFlow

    override suspend fun addTimeZone(timeZone: String) {
        // 模擬新增時區
        val currentList = currentTimesFlow.value.toMutableList()
        currentList.add(
            currentTimeTestData,
        )
        currentTimesFlow.value = currentList
    }

    override suspend fun deleteTimeZones(timeZones: List<String>) {
        // 模擬刪除行為
        val filtered = currentTimesFlow.value.filterNot { it.timeZone in timeZones }
        currentTimesFlow.value = filtered
    }

    override suspend fun updateTimeZone(previousTimeZone: String, updatedTimeZone: String) {
        // 模擬更新行為
        val updated = currentTimesFlow.value.map {
            if (it.timeZone == previousTimeZone) it.copy(timeZone = updatedTimeZone) else it
        }
        currentTimesFlow.value = updated
    }
}
