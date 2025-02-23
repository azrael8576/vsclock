package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.data.model.asEntity
import com.wei.vsclock.core.database.dao.CurrentTimeDao
import com.wei.vsclock.core.database.model.CurrentTimeEntity
import com.wei.vsclock.core.database.model.asExternalModel
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.network.VsclockNetworkDataSource
import com.wei.vsclock.core.result.DataSourceResult
import com.wei.vsclock.core.result.asDataSourceResultWithRetry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * Implementation of the [TimeRepository].
 *
 *  @param ioDispatcher 用於執行 IO 相關操作的 CoroutineDispatcher。
 * @param network 數據源的網路接口。
 */
class DefaultTimeRepository
@Inject
constructor(
    @Dispatcher(VsclockDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val currentTimeDao: CurrentTimeDao,
    private val network: VsclockNetworkDataSource,
) : TimeRepository {
    override suspend fun checkHealth(): Flow<Int> =
        withContext(ioDispatcher) {
            flow {
                emit(network.checkHealth().code())
            }
        }

    /**
     * **刷新資料庫中所有時區的當前時間資訊**
     *
     * 此方法從資料庫中取得所有 [CurrentTimeEntity] 記錄，並透過 API 取得最新的時間資訊。
     * 為了確保 API 穩定性，每次 API 呼叫最多會 **重試 3 次**，並且採用 **併發處理** 來加速更新過程。
     *
     * ### **更新邏輯**
     * 1. 取得資料庫中的所有時區記錄。
     * 2. 針對每個時區，併發呼叫 API 取得最新時間資訊，使用 `asDataSourceResultWithRetry` 進行最多 **3 次重試**。
     * 3. 若 API 回應成功：
     *    - 將 API 回傳的資料轉換為 `CurrentTimeEntity`。
     *    - **保留原有的 `id`、`createdAt` 及 `timeZone`**，因為這些欄位不會由 API 提供。
     * 4. 若 API 失敗，則不更新該筆資料（保持原狀）。
     * 5. 最後，僅將 **成功更新** 的資料批次寫入資料庫。
     *
     * ### **設計考量**
     * - **資料庫為唯一真實來源（Single Source of Truth, SSOT）**：
     *   確保 API 回應僅用於更新部分資料，而不會覆蓋原本的時區資訊或 ID。
     * - **API 不穩定時的容錯處理**：
     *   透過 `asDataSourceResultWithRetry` 避免短暫網路問題影響所有資料更新。
     * - **併發更新以提高效能**：
     *   透過 `coroutineScope` 及 `async`，一次性發送多個 API 請求，加速更新過程。
     *
     * @throws Exception 若 API 呼叫或資料庫更新發生意外錯誤，則會拋出異常。
     */
    override suspend fun refreshCurrentTimes() = withContext(ioDispatcher) {
        // 取得 DB 中所有時鐘記錄（快照）
        val dbEntities = currentTimeDao.getCurrentTimeEntities().first()

        // 針對每筆 DB 資料進行 API 更新，並發執行，若失敗則回傳 null
        val updatedEntities = coroutineScope {
            dbEntities.map { entity ->
                async {
                    // 呼叫 API 並加上重試機制
                    val networkResult = flow {
                        emit(network.getCurrentTime(entity.timeZone))
                    }.asDataSourceResultWithRetry(
                        maxRetries = 3,
                        traceTag = "network.getCurrentTime: ${entity.timeZone}",
                    ).first { it !is DataSourceResult.Loading }

                    when (networkResult) {
                        is DataSourceResult.Success -> {
                            // 轉換 API 回傳資料為 DB Entity，保留原有 id、timeZone 與 createdAt
                            networkResult.data.asEntity(
                                id = entity.id,
                                timeZone = entity.timeZone,
                                createdAt = entity.createdAt,
                            )
                        }
                        // API 呼叫失敗時回傳 null，不更新此筆資料
                        else -> null
                    }
                }
            }.awaitAll()
        }

        // 過濾掉 API 失敗（null）的項目，將成功更新的資料批次寫回資料庫
        updatedEntities.filterNotNull().forEach { updatedEntity ->
            currentTimeDao.updateCurrentTimeEntity(updatedEntity)
        }
    }

    override suspend fun getAvailableTimeZones(): Flow<List<String>> =
        withContext(ioDispatcher) {
            flow {
                emit(network.getAvailableTimeZones())
            }
        }

    override suspend fun getCurrentTimes(): Flow<List<CurrentTime>> =
        currentTimeDao.getCurrentTimeEntities()
            .map { it.map(CurrentTimeEntity::asExternalModel) }

    override suspend fun addTimeZone(timeZone: String) =
        withContext(ioDispatcher) {
            currentTimeDao.insertCurrentTimeEntity(
                CurrentTimeEntity(
                    timeZone = timeZone,
                    createdAt = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
                ),
            )
        }

    override suspend fun deleteTimeZones(timeZones: List<String>) =
        currentTimeDao.deleteCurrentTimeEntities(timeZones)

    override suspend fun updateTimeZone(previousTimeZone: String, updatedTimeZone: String) =
        withContext(ioDispatcher) {
            val existingEntity = currentTimeDao.getCurrentTimeEntityByTimeZone(previousTimeZone)
            if (existingEntity != null) {
                val updatedEntity = existingEntity.copy(
                    timeZone = updatedTimeZone,
                    year = 0,
                    month = 0,
                    day = 0,
                    hour = 0,
                    minute = 0,
                    seconds = 0,
                    milliseconds = 0,
                    dateTime = "",
                    date = "",
                    time = "",
                    dayOfWeek = "",
                )
                currentTimeDao.updateCurrentTimeEntity(updatedEntity)
            }
        }
}
