package com.example.myapplication.RemoteDB

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDbModule {
    companion object {
        private var instance: FirebaseFirestore? = null
    }

    @Provides
    @Singleton
    fun provideDbInstance() : FirebaseFirestore {
        if(instance == null) {
            instance = Firebase.firestore
        }
        return instance!!
    }
}