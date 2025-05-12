package com.argentspirit.quickvault.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.System.currentTimeMillis

@Entity(tableName = "services")
data class Service(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceName: String,
    val creationDate: Long = currentTimeMillis()
)
