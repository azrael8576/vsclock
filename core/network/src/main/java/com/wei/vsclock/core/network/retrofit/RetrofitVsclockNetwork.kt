package com.wei.vsclock.core.network.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.wei.vsclock.core.network.BuildConfig
import com.wei.vsclock.core.network.VsclockNetworkDataSource
import com.wei.vsclock.core.network.model.NetworkCurrentTime
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for VsClock Network API
 */
interface RetrofitVsclockNetworkApi {
    @GET("api/health/check")
    suspend fun checkHealth(): retrofit2.Response<Unit>

    /**
     * ${BACKEND_URL}/api/time/current/zone?timeZone=Europe/Amsterdam
     */
    @GET("api/time/current/zone")
    suspend fun getCurrentTime(
        @Query("timeZone") timeZone: String,
    ): NetworkCurrentTime

    @GET("api/timezone/availabletimezones")
    suspend fun getAvailableTimeZones(): List<String>
}

private const val VSCLOCK_BASE_URL = BuildConfig.BACKEND_URL

/**
 * [Retrofit] backed [VsclockNetworkDataSource]
 */
@Singleton
class RetrofitVsclockNetwork
@Inject
constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : VsclockNetworkDataSource {
    private val networkApi =
        Retrofit.Builder()
            .baseUrl(VSCLOCK_BASE_URL)
            .callFactory(okhttpCallFactory)
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitVsclockNetworkApi::class.java)

    override suspend fun checkHealth(): Response<Unit> {
        return networkApi.checkHealth()
    }

    override suspend fun getCurrentTime(timeZone: String): NetworkCurrentTime {
        return networkApi.getCurrentTime(timeZone)
    }

    override suspend fun getAvailableTimeZones(): List<String> {
        return networkApi.getAvailableTimeZones()
    }
}
