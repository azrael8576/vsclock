package com.wei.vsclock.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wei.vsclock.core.database.model.CurrentTimeEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [CurrentTime] and [CurrentTimeEntity] access
 */
@Dao
interface CurrentTimeDao {

    /**
     * 新增時區
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentTimeEntity(currentTimeEntity: CurrentTimeEntity)

    /**
     * 刪除時區
     *
     * Deletes rows in the db matching the specified [timeZone]
     */
    @Query(
        value = """
            DELETE FROM current_times
            WHERE timeZone in (:timeZone)
        """,
    )
    suspend fun deleteCurrentTimeEntity(timeZone: String)

    /**
     * 查找特定 CurrentTimeEntity by :timeZone
     */
    @Query("SELECT * FROM current_times WHERE timeZone = :timeZone LIMIT 1")
    suspend fun getCurrentTimeEntityByTimeZone(timeZone: String): CurrentTimeEntity?

    /**
     * 更新現有資料
     */
    @Update
    suspend fun updateCurrentTimeEntity(currentTimeEntity: CurrentTimeEntity)

    @Query(
        value = """
        SELECT * FROM current_times
        ORDER BY createdAt ASC
    """,
    )
    fun getCurrentTimeEntities(): Flow<List<CurrentTimeEntity>>

    @Query(value = "DELETE FROM current_times")
    suspend fun clearCurrentTimeEntities()
}
