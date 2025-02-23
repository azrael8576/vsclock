package com.wei.vsclock.core.data.model

import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.model.NetworkCurrentTime
import kotlinx.datetime.Instant

// TODO Wei: Use Entity::asExternalModel make sure SSOT!
fun NetworkCurrentTime.asExternalModelTemp() =
    CurrentTime(
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
        timeZone = this.timeZone ?: "",
        dayOfWeek = this.dayOfWeek,
        createdAt = Instant.fromEpochMilliseconds(0L),
    )
