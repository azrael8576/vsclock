package com.wei.vsclock.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val vsclockDispatcher: VsclockDispatchers)

enum class VsclockDispatchers {
    Default,
    IO,
}
