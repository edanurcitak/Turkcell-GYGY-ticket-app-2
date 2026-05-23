package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Ekranın o anki durumunu tutan State sınıfı
data class TicketDetailState(
    val isLoading: Boolean = false,
    val ticket: Ticket? = null,
    val errorMessage: String? = null
)

class TicketDetailViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailState())
    val state: StateFlow<TicketDetailState> = _state.asStateFlow()

    fun loadTicketDetail(id: String) {
        viewModelScope.launch {
            // Yükleniyor durumuna geç
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            // ID'ye göre bileti iste
            ticketRepository.getTicketById(id).fold(
                onSuccess = { fetchedTicket ->
                    // Başarılıysa bileti state'e kaydet
                    _state.value = _state.value.copy(
                        isLoading = false,
                        ticket = fetchedTicket
                    )
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Bilet detayı yüklenemedi."
                    )
                }
            )
        }
    }
}