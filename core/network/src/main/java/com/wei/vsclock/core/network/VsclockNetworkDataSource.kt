package com.wei.vsclock.core.network

import com.wei.vsclock.core.network.model.NetworkCurrentTime

/**
 * Interface representing network calls to the VsClock backend
 */
interface VsclockNetworkDataSource {
    suspend fun checkHealth(): retrofit2.Response<Unit>

    suspend fun getCurrentTime(
        timeZone: String,
    ): NetworkCurrentTime

    suspend fun getAvailableTimeZones(): List<String>
}
