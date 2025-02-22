package com.wei.vsclock.core.data.repository

import com.wei.vsclock.core.model.data.CurrentTime
import kotlinx.coroutines.flow.Flow

interface TimeRepository {
    suspend fun checkHealth(): Flow<Int>

    suspend fun getCurrentTime(
        timeZone: String,
    ): Flow<CurrentTime>

    suspend fun getAvailableTimeZones(): Flow<List<String>>

    suspend fun getCurrentTimes(): Flow<List<CurrentTime>>

    suspend fun addTimeZone(timeZone: String)

    suspend fun deleteTimeZones(timeZones: List<String>)

    suspend fun updateTimeZone(
        previousTimeZone: String,
        updatedTimeZone: String,
    )
}
