package com.argentspirit.quickvault.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.argentspirit.quickvault.entities.PasswordEntry

@Dao
interface PasswordEntriesDao {
    @Insert
    suspend fun insertPasswordEntry(entry: PasswordEntry): Long
    @Update
    suspend fun updatePasswordEntry(entry: PasswordEntry)
    @Delete
    suspend fun deletePasswordEntry(entry: PasswordEntry)
}