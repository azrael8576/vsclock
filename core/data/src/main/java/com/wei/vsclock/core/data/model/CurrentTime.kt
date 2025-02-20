package com.wei.vsclock.core.data.model

import com.wei.vsclock.core.model.data.CurrentTime
import com.wei.vsclock.core.network.model.NetworkCurrentTime

fun NetworkCurrentTime.asExternalModel() =
    CurrentTime(
        year = this.year,
        month = this.month,
        day = this.day,
        hour = this.hour,
        minute = this.minute,
        seconds = this.seconds,
        milliseconds = this.milliseconds,
        dateTime = this.dateTime,
        date = this.date,
        time = this.time,
        timeZone = this.timeZone,
        dayOfWeek = this.dayOfWeek,
        isDstActive = this.isDstActive,
    )
