package com.wei.vsclock.core.network.di

import com.wei.vsclock.core.network.Dispatcher
import com.wei.vsclock.core.network.VsclockDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    /**
     * 提供 IO CoroutineDispatcher 的實例，可以在需要進行 IO 操作的協程中使用。
     *
     * @return IO CoroutineDispatcher 的實例
     */
    @Provides
    @Dispatcher(VsclockDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * 提供 Default CoroutineDispatcher 的實例，可以在需要進行 CPU 密集型工作的協程中使用。
     *
     * @return Default CoroutineDispatcher 的實例
     */
    @Provides
    @Dispatcher(VsclockDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
