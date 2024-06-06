package com.example.myapplication.LocalDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BaseDao {
    @Query("SELECT id FROM basedata WHERE name == :name")
    fun searchByName(name: String): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg base: BaseData)


    @Query("SELECT * FROM basedata WHERE name == :baseName")
    fun searchCockTailByBaseName(baseName: String): CockTailByBase

    @Query("SELECT name FROM basedata WHERE id == :id")
    fun findById(id: Int): String
}