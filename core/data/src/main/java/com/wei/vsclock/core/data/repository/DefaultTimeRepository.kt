package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.data.model.asExternalModelTemp
import com.wei.vsclock.core.database.dao.CurrentTimeDao
import com.wei.vsclock.core.database.model.CurrentTimeEntity
import com.wei.vsclock.core.database.model.asExternalModel
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.network.VsclockNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    override suspend fun getCurrentTime(timeZone: String): Flow<CurrentTime> =
        withContext(ioDispatcher) {
            flow {
                emit(network.getCurrentTime(timeZone).asExternalModelTemp())
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
