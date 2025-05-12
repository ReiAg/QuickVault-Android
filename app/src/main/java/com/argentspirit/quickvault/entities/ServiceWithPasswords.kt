package com.argentspirit.quickvault.entities

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceWithPasswords(
    @Embedded val service: Service, // Embeds the columns from the Service entity directly
    @Relation(
        parentColumn = "id", // The primary key column in the "parent" entity (Service)
        entityColumn = "serviceId" // The foreign key column in the "child" entity (PasswordEntry) that references the parent
    )
    val passwordEntries: List<PasswordEntry> // The list of PasswordEntry entities associated with this Service
)
