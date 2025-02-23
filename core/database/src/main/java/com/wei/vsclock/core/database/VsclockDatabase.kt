package com.wei.vsclock.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wei.vsclock.core.database.dao.CurrentTimeDao
import com.wei.vsclock.core.database.model.CurrentTimeEntity
import com.wei.vsclock.core.database.util.InstantConverter

@Database(
    entities = [
        CurrentTimeEntity::class,
    ],
    version = 1,
//    autoMigrations = [
//        ,
//
//    ],
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
)
internal abstract class VsclockDatabase : RoomDatabase() {
    abstract fun currentTimeDao(): CurrentTimeDao
}
