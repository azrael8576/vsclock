package com.wei.vsclock.core.data.model

enum class RefreshRate(val second: Long) {
    MIN_1(second = 1 * 60L),
    MIN_5(second = 5 * 60L),
    MIN_10(second = 10 * 60L),
}
