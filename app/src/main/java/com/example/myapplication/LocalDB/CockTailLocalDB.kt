package com.example.myapplication.LocalDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CockTailData::class, BaseData::class], version = 1)
abstract class CockTailLocalDB : RoomDatabase() {
    abstract fun getCocktailDao(): CockTailDao
    abstract fun getBaseDao(): BaseDao
}