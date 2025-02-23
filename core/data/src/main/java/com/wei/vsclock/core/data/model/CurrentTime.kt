package com.wei.vsclock.core.data.model

import com.wei.vsclock.core.database.model.CurrentTimeEntity
import com.wei.vsclock.core.network.model.NetworkCurrentTime
import kotlinx.datetime.Instant

fun NetworkCurrentTime.asEntity(id: Int, timeZone: String, createdAt: Instant) =
    CurrentTimeEntity(
        id = id,
        year = this.year,
        month = this.month,
        day = this.day,
        hour = this.hour,
        minute = this.minute,
        seconds = this.seconds,
        milliseconds = this.milliseconds,
        dateTime = this.dateTime,
        date = this.date ?: "",
        time = this.time ?: "",
        timeZone = this.timeZone ?: timeZone,
        dayOfWeek = this.dayOfWeek,
        createdAt = createdAt,
    )
