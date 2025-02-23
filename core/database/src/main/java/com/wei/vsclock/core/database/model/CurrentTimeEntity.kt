package com.wei.vsclock.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.wei.vsclock.core.model.data.CurrentTime
import kotlinx.datetime.Instant

/**
 * Defines an VsClock times resource.
 */
@Entity(
    tableName = "current_times",
)
data class CurrentTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val seconds: Int = 0,
    val milliseconds: Int = 0,
    @ColumnInfo(defaultValue = "")
    val dateTime: String = "",
    @ColumnInfo(defaultValue = "")
    val date: String = "",
    @ColumnInfo(defaultValue = "")
    val time: String = "",
    @ColumnInfo(defaultValue = "")
    val timeZone: String = "",
    @ColumnInfo(defaultValue = "")
    val dayOfWeek: String = "",
    // 依照創建時間排序
    val createdAt: Instant,
)

fun CurrentTimeEntity.asExternalModel() = CurrentTime(
    year = year,
    month = month,
    day = day,
    hour = hour,
    minute = minute,
    seconds = seconds,
    milliseconds = milliseconds,
    dateTime = dateTime,
    date = date,
    time = time,
    timeZone = timeZone,
    dayOfWeek = dayOfWeek,
    createdAt = createdAt,
)
