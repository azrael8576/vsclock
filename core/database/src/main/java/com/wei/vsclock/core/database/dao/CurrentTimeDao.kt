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
     * 取得所有 `CurrentTimeEntity`，依照 `createdAt` 時間遞增排序。
     *
     * @return Flow<List<CurrentTimeEntity>> 觀察 `current_times` 資料表的變化。
     */
    @Query(
        value = """
        SELECT * FROM current_times
        ORDER BY createdAt ASC
    """,
    )
    fun getCurrentTimeEntities(): Flow<List<CurrentTimeEntity>>

    /**
     * 新增時區記錄。
     *
     * @param currentTimeEntity 欲插入的 `CurrentTimeEntity` 物件。
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentTimeEntity(currentTimeEntity: CurrentTimeEntity)

    /**
     * 根據提供的時區列表刪除對應的記錄。
     *
     * - 若 `timeZones` 中的某個時區不存在於資料庫，則該條目不會有影響。
     *
     * @param timeZones 欲刪除的時區列表。
     */
    @Query(
        """
        DELETE FROM current_times
        WHERE timeZone IN (:timeZones)
    """,
    )
    suspend fun deleteCurrentTimeEntities(timeZones: List<String>)

    /**
     * 根據 `timeZone` 查詢對應的 `CurrentTimeEntity`。
     *
     * - 若 `timeZone` 存在，則返回對應的 `CurrentTimeEntity` 物件。
     * - 若 `timeZone` 不存在，則回傳 `null`。
     *
     * @param timeZone 欲查詢的時區。
     * @return `CurrentTimeEntity?` 若存在則回傳該記錄，否則回傳 `null`。
     */
    @Query("SELECT * FROM current_times WHERE timeZone = :timeZone LIMIT 1")
    fun getCurrentTimeEntityByTimeZone(timeZone: String): Flow<CurrentTimeEntity?>

    /**
     * 更新 `current_times` 資料表中指定的 `CurrentTimeEntity` 記錄。
     *
     * - **注意**：僅會更新 `currentTimeEntity` 內指定的欄位，未提供的欄位將保持不變。
     *
     * @param currentTimeEntity 欲更新的 `CurrentTimeEntity` 物件。
     */
    @Update
    suspend fun updateCurrentTimeEntity(currentTimeEntity: CurrentTimeEntity)
}
