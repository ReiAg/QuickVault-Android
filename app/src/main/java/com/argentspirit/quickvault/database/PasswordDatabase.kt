package com.argentspirit.quickvault.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.argentspirit.quickvault.database.dao.PasswordEntriesDao
import com.argentspirit.quickvault.database.dao.ServicesDao
import com.argentspirit.quickvault.entities.PasswordEntry
import com.argentspirit.quickvault.entities.Service

@Database(entities = [Service::class, PasswordEntry::class], version = 1, exportSchema = false)
abstract class PasswordDatabase : RoomDatabase() {
    abstract fun servicesDao(): ServicesDao
    abstract fun passwordEntriesDao(): PasswordEntriesDao
}