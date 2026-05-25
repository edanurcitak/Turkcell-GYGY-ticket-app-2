package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.turkcell.data.network.ApiException
import com.turkcell.data.network.NetworkException
import com.turkcell.core.util.getAuthErrorMessage
import com.turkcell.core.util.NETWORK_ERROR_MESSAGE
import com.turkcell.core.util.UNKNOWN_ERROR_MESSAGE

data class RegisterUiState(
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false
) {
    // Bütün kutular dolu olmalı, şifre en az 8 karakter olmalı ve şifreler eşleşmeli, o zaman buton aktifleşir
    val canSubmit: Boolean get() = email.isNotBlank() &&
            name.isNotBlank() &&
            surname.isNotBlank() &&
            password.length >= 8 &&
            password == passwordConfirm &&
            !isLoading
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()


    fun onNameChange(value: String) = _state.update { it.copy(name = value, errorMessage = null) }
    fun onSurnameChange(value: String) = _state.update { it.copy(surname = value, errorMessage = null) }
    fun onPhoneChange(value: String) = _state.update { it.copy(phone = value, errorMessage = null) }
    fun onEmailChange(value: String) = _state.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, errorMessage = null) }
    fun onPasswordConfirmChange(value: String) = _state.update { it.copy(passwordConfirm = value, errorMessage = null) }
    fun consumeError() = _state.update { it.copy(errorMessage = null) }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.register(
                name = current.name,
                surname = current.surname,
                phone = current.phone,
                email = current.email,
                password = current.password
            )
                .onSuccess { _state.update { it.copy(isLoading = false, isRegistered = true) } }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException -> getAuthErrorMessage(error.code)
                        is NetworkException -> NETWORK_ERROR_MESSAGE
                        else -> error.message ?: UNKNOWN_ERROR_MESSAGE
                    }

                    _state.update { it.copy(isLoading = false, errorMessage = message) }
                }
        }
    }
}