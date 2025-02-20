package com.wei.vsclock.core.network.di

import com.wei.vsclock.core.network.VsclockNetworkDataSource
import com.wei.vsclock.core.network.retrofit.RetrofitVsclockNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface FlavoredNetworkModule {

    @Binds
    fun binds(implementation: RetrofitVsclockNetwork): VsclockNetworkDataSource
}
