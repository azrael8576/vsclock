package com.wei.vsclock.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network representation of [CurrentTime] when fetched from /api/time/current/zone
 * v0.99.4
 *
 * @property year Year (integer $int32). Example: 2020
 * @property month Month (integer $int32). Example: 12
 * @property day Day (integer $int32). Example: 13
 * @property hour Hour of the day in range 0–24 (integer $int32). Example: 9
 * @property minute Minute (integer $int32). Example: 30
 * @property seconds Second (integer $int32). Example: 17
 * @property milliseconds Milliseconds (integer $int32). Example: 0
 * @property dateTime Full date-time in `string($date-time)` format. Example: 2020-12-13T09:30:17
 * @property date Date string (nullable). Example: 13/12/2020
 * @property time Time string (nullable). Example: 09:30
 * @property timeZone Time zone of the result (nullable). Example: America/Los_Angeles
 * @property dayOfWeek Day of week (`DayOfWeek string`). Example: Sunday
 * @property isDstActive Indicates whether DST is applied and active (boolean). Example: false
 */
@Serializable
data class NetworkCurrentTime(
    @SerialName("year")
    val year: Int,

    @SerialName("month")
    val month: Int,

    @SerialName("day")
    val day: Int,

    @SerialName("hour")
    val hour: Int,

    @SerialName("minute")
    val minute: Int,

    @SerialName("seconds")
    val seconds: Int,

    @SerialName("milliSeconds")
    val milliseconds: Int,

    @SerialName("dateTime")
    val dateTime: String,

    @SerialName("date")
    val date: String? = null,

    @SerialName("time")
    val time: String? = null,

    @SerialName("timeZone")
    val timeZone: String? = null,

    @SerialName("dayOfWeek")
    val dayOfWeek: String,

    @SerialName("dstActive")
    val isDstActive: Boolean,
)
