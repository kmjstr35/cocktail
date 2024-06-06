package com.example.myapplication.LocalDB

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// android documentation highly recommend that using room persistent library instead of directly using sqlite api
// reference : https://developer.android.com/training/data-storage/sqlite

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    // singleton pattern
    companion object {
        private var instance: CockTailLocalDB? = null
    }

    @Provides
    @Singleton
    fun provideDbInstance(@ApplicationContext ctx : Context) : CockTailLocalDB {
        if(instance == null) {
            instance = Room.databaseBuilder(ctx, CockTailLocalDB::class.java, "main")
                .allowMainThreadQueries()
                .build()
        }
        return instance!!
    }
}