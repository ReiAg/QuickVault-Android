package com.argentspirit.quickvault.hilt

import android.content.Context
import androidx.room.Room
import com.argentspirit.quickvault.database.PasswordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Singleton {
    companion object {
        private const val DATABASE_NAME = "app_database.db"
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): PasswordDatabase{
        return Room.databaseBuilder(context, PasswordDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(false)
            .build()
    }
}