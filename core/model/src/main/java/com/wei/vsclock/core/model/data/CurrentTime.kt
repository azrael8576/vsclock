package com.wei.vsclock.core.model.data

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
    val date: String? = null,
    val time: String? = null,
    val timeZone: String? = null,
    val dayOfWeek: String,
    val isDstActive: Boolean,
)
