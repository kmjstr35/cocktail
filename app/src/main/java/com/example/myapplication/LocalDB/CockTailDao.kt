package com.example.myapplication.LocalDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CockTailDao {
    @Query("SELECT * FROM cocktaildata ORDER BY rating DESC")
    fun sortByRating(): LiveData<List<CockTailData>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg cocktail : CockTailData)

    @Query("SELECT * FROM cocktaildata WHERE id == :id")
    fun findRowById(id: Int) : LiveData<CockTailData>

    @Query("SELECT * FROM cocktaildata WHERE name LIKE :name ORDER BY rating DESC")
    fun searchByName(name: String): Flow<CockTailDataWithBase>

    @Query("SELECT * FROM cocktaildata WHERE id == :id")
    fun getFullDataById(id: Int) : LiveData<CockTailDataWithBase>
    @Update
    fun Update(data: CockTailData)

}