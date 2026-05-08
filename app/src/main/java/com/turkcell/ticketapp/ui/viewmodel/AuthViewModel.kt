package com.turkcell.ticketapp.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var sonucMesaji = mutableStateOf("")

    fun girisYap() {
        viewModelScope.launch {
            val result = authRepository.login(email.value, password.value)

            result.onSuccess {
                sonucMesaji.value = "Başarılı!"
            }.onFailure { error ->
                sonucMesaji.value = "Hata oluştu: ${error.message}"
            }
        }
    }
}