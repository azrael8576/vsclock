package com.wei.vsclock.core.model.data

import kotlinx.datetime.Instant

/**
 * External data layer representation of a VsClock time resource
 */
data class CurrentTime(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val seconds: Int,
    val milliseconds: Int,
    val dateTime: String,
    val date: String,
    val time: String,
    val timeZone: String,
    val dayOfWeek: String,
    val createdAt: Instant,
)
