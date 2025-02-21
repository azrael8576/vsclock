package com.wei.vsclock.core

enum class AppLocale(val code: String) {
    EN("en"),
    ZH_HANT_TW("zh-Hant-TW"),
    ;

    companion object {
        fun fromCode(code: String): AppLocale {
            return entries.find { it.code == code } ?: EN
        }
    }
}
