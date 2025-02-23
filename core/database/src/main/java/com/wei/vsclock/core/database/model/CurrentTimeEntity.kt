package com.wei.vsclock.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val seconds: Int,
    val milliseconds: Int,
    val dateTime: String,
    @ColumnInfo(defaultValue = "")
    val date: String,
    @ColumnInfo(defaultValue = "")
    val time: String,
    @ColumnInfo(defaultValue = "")
    val timeZone: String,
    val dayOfWeek: String,
    // 依照創建時間排序
    val createdAt: Instant,
)
