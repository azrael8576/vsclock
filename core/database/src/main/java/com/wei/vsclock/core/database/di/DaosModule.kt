package com.wei.vsclock.core.database.di

import com.wei.vsclock.core.database.VsclockDatabase
import com.wei.vsclock.core.database.dao.CurrentTimeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun providesCurrentTimeDao(
        database: VsclockDatabase,
    ): CurrentTimeDao = database.currentTimeDao()
}
