package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.data.model.asExternalModel
import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import com.wei.vsclock.core.network.VsclockNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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
                emit(network.getCurrentTime(timeZone).asExternalModel())
            }
        }

    override suspend fun getAvailableTimeZones(): Flow<List<String>> =
        withContext(ioDispatcher) {
            flow {
                emit(network.getAvailableTimeZones())
            }
        }
}
