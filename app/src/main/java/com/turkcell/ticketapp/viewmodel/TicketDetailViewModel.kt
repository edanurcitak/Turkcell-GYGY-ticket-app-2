package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.purchase.Ticket
import com.turkcell.core.domain.event.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketDetailState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
    val errorMessage: String? = null
)

class TicketDetailViewModel(
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketDetailState())
    val state: StateFlow<TicketDetailState> = _state.asStateFlow()

    fun loadTicketsByType(ticketTypeId: String) {
        if (_state.value.isLoading || _state.value.isRefreshing) return
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        fetchTickets(ticketTypeId)
    }

    fun refreshTicketsByType(ticketTypeId: String) {
        if (_state.value.isLoading || _state.value.isRefreshing) return
        _state.update { it.copy(isRefreshing = true, errorMessage = null) }
        fetchTickets(ticketTypeId)
    }

    private fun fetchTickets(ticketTypeId: String) {
        viewModelScope.launch {
            ticketRepository.getMyTickets().fold(
                onSuccess = { allTickets ->
                    val filteredTickets = allTickets.filter { it.ticketTypeId == ticketTypeId }
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            tickets = filteredTickets
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }
}