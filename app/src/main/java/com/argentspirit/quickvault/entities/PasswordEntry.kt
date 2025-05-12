package com.argentspirit.quickvault.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.lang.System.currentTimeMillis

@Entity(
    tableName = "password_entries",
    foreignKeys = [
        ForeignKey(
            entity = Service::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PasswordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceId: Long,
    val username: String?,
    val password: String,
    val url: String?,
    val creationDate: Long = currentTimeMillis()
)
