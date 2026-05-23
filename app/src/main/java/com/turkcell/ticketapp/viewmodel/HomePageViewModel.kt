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

// 1. Ekrana (UI) sadece ihtiyacı olanı vereceğimiz yeni, basit modelimiz
data class TicketUiItem(
    val ticketId: String,
    val displayTitle: String
)

data class HomePageUiState(
    val events: List<Event> = emptyList(),
    val rawTickets: List<Ticket> = emptyList(),
    val myTickets: List<TicketUiItem> = emptyList(),
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
                    _state.update { currentState ->
                        val mappedTickets = mapTicketsToUiItems(currentState.rawTickets, eventList)
                        currentState.copy(
                            events = eventList,
                            myTickets = mappedTickets,
                            isLoadingEvents = false
                        )
                    }
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
                    _state.update { currentState ->
                        val mappedTickets = mapTicketsToUiItems(ticketList, currentState.events)
                        currentState.copy(
                            rawTickets = ticketList,
                            myTickets = mappedTickets,
                            isLoadingTickets = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.message ?: "Biletler yüklenemedi", isLoadingTickets = false) }
                }
        }
    }

    private fun mapTicketsToUiItems(tickets: List<Ticket>, events: List<Event>): List<TicketUiItem> {
        return tickets.map { ticket ->
            val matchedEvent = events.find { event ->
                event.ticketTypes.any { it.id == ticket.ticketTypeId }
            }

            val matchedTicketType = matchedEvent?.ticketTypes?.find { it.id == ticket.ticketTypeId }

            val displayTitle = matchedTicketType?.name ?: matchedEvent?.name ?: "Bilet: ${ticket.id.take(5)}..."

            TicketUiItem(
                ticketId = ticket.id,
                displayTitle = displayTitle
            )
        }
    }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }
}