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


data class LoginUiState(val email: String = "",
                        val password: String = "",
                        val isLoading: Boolean = false,
                        val errorMessage: String? = null,
                        val isLoggedIn: Boolean = false
) {
    val canSubmit: Boolean get() = email.isNotBlank() && password.length >= 8 && !isLoading
}

class LoginViewModel(
    private val authRepository: AuthRepository //bağımlılık
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) =
        _state.update { it.copy(password = value, errorMessage = null) }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }

    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.login(current.email, current.password)
                .onSuccess { _state.update { it.copy(isLoading = false, isLoggedIn = true) } }
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