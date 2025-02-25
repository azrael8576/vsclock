package com.wei.vsclock.core.testing.data

import com.wei.vsclock.core.model.data.CurrentTime
import kotlinx.datetime.Instant

val currentTimeTestData: CurrentTime = CurrentTime(
    year = 2025,
    month = 1,
    day = 1,
    hour = 12,
    minute = 0,
    seconds = 0,
    milliseconds = 0,
    dateTime = "2025-01-01T12:00:00",
    date = "2025-01-01",
    time = "12:00",
    timeZone = "Asia/Taipei",
    dayOfWeek = "Wednesday",
    createdAt = Instant.fromEpochMilliseconds(0L),
)
