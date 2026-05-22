package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomePageUiState(
    val events: List<Event> = emptyList(),
    val myTickets: List<Ticket> = emptyList(),
    val isLoadingEvents: Boolean = false,
    val isLoadingTickets: Boolean = false,
    val errorMessage: String? = null
)

class HomePageViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomePageUiState())
    val state: StateFlow<HomePageUiState> = _state.asStateFlow()

    init {
        fetchEvents()
        fetchMyTickets()
    }

    private fun fetchEvents() {
        _state.update { it.copy(isLoadingEvents = true, errorMessage = null) }

        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { eventList ->
                    _state.update { it.copy(events = eventList, isLoadingEvents = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Etkinlikler yüklenemedi", isLoadingEvents = false) }
                }
        }
    }

    private fun fetchMyTickets() {
        _state.update { it.copy(isLoadingTickets = true, errorMessage = null) }

        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { ticketList ->
                    _state.update { it.copy(myTickets = ticketList, isLoadingTickets = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Biletler yüklenemedi", isLoadingTickets = false) }
                }
        }
    }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }
}