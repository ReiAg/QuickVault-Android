package com.argentspirit.quickvault.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.lang.System.currentTimeMillis

@Entity(
    tableName = "services",
    indices = [Index(value = ["serviceName"], unique = true)]
)
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceName: String,
    val creationDate: Long = currentTimeMillis()
)
