package ru.netology.maps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.maps.entity.MarkerEntity

@Dao
interface MarkerDao {

    @Query("SELECT * FROM MarkerEntity ORDER BY id DESC")
    fun getAll(): Flow<List<MarkerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marker: MarkerEntity)

    @Query("DELETE FROM MarkerEntity WHERE id = :id")
    suspend fun removeById(id: Int)
}