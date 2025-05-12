package com.argentspirit.quickvault.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argentspirit.quickvault.database.PasswordDatabase
import com.argentspirit.quickvault.entities.PasswordEntry
import com.argentspirit.quickvault.entities.Service
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ServicesViewModel @Inject constructor(private val database: PasswordDatabase): ViewModel() {
    val services: StateFlow<List<Service>> = database.servicesDao().getAllServices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Configure how long to keep the flow alive
            initialValue = emptyList() // Initial empty list
        )
    fun AddPassword(serviceName: String, userName: String?, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            val serviceId = database.servicesDao().insertService(Service(serviceName = serviceName))
            database.passwordEntriesDao().insertPasswordEntry(PasswordEntry(
                serviceId = serviceId,
                username = userName,
                password = password,
                url = null
            ))
        }
    }
}