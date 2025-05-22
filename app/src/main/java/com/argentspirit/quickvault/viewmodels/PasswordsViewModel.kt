package com.argentspirit.quickvault.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argentspirit.quickvault.database.PasswordDatabase
import com.argentspirit.quickvault.entities.ServiceWithPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PasswordsViewModel  @Inject constructor(
    private val database: PasswordDatabase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val serviceId: StateFlow<Long?> = savedStateHandle.getStateFlow("serviceId", null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val service: StateFlow<ServiceWithPasswords?> = serviceId.filterNotNull().flatMapLatest {
        database.servicesDao().getServiceWithPasswords(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

}