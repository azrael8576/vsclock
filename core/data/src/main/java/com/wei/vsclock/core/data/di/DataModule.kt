package com.wei.vsclock.core.data.di

import com.wei.vsclock.core.data.repository.DefaultTimeRepository
import com.wei.vsclock.core.data.repository.TimeRepository
import com.wei.vsclock.core.data.utils.ConnectivityManagerNetworkMonitor
import com.wei.vsclock.core.data.utils.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsTimeRepository(
        timeRepository: DefaultTimeRepository,
    ): TimeRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(networkMonitor: ConnectivityManagerNetworkMonitor): NetworkMonitor
}
