package com.argentspirit.quickvault.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.argentspirit.quickvault.entities.Service
import com.argentspirit.quickvault.entities.ServiceWithPasswords
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignore if serviceName already exists (optional strategy)
    suspend fun insertService(service: Service): Long

    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<Service>> // Observe changes to all services

    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceById(serviceId: Long): Flow<Service?>

    @Delete
    suspend fun deleteService(service: Service)

    @Transaction
    @Query("SELECT * FROM services WHERE id = :serviceId")
    fun getServiceWithPasswords(serviceId: Long): Flow<ServiceWithPasswords?>
}