package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.purchase.Ticket
import com.turkcell.core.domain.event.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketUiItem(
    val ticketTypeId: String,
    val displayTitle: String,
    val count: Int
)

data class HomePageUiState(
    val events: List<Event> = emptyList(),
    val rawTickets: List<Ticket> = emptyList(),
    val myTickets: List<TicketUiItem> = emptyList(),
    val isLoadingEvents: Boolean = false,
    val isRefreshingEvents: Boolean = false,
    val isLoadingTickets: Boolean = false,
    val isRefreshingTickets: Boolean = false,
    val errorMessage: String? = null
)

class HomePageViewModel(
    private val eventRepository: EventRepository,
    private val ticketRepository: TicketRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomePageUiState())
    val state: StateFlow<HomePageUiState> = _state.asStateFlow()

    init {
        fetchEvents()
        fetchMyTickets()
    }

    fun loadEvents() {
        if (_state.value.isLoadingEvents || _state.value.isRefreshingEvents) return
        _state.update { it.copy(isLoadingEvents = true, errorMessage = null) }
        fetchEvents()
    }

    private fun refreshEvents() {
        if (_state.value.isLoadingEvents || _state.value.isRefreshingEvents) return
        _state.update { it.copy(isRefreshingEvents = true, errorMessage = null) }
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { eventList ->
                    _state.update { currentState ->
                        val mappedTickets = mapTicketsToUiItems(currentState.rawTickets, eventList)
                        currentState.copy(
                            events = eventList,
                            myTickets = mappedTickets,
                            isLoadingEvents = false,
                            isRefreshingEvents = false // Yükleme bitince refresh'i de kapat
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.message ?: "Etkinlikler yüklenemedi",
                            isLoadingEvents = false,
                            isRefreshingEvents = false
                        )
                    }
                }
        }
    }

    fun loadMyTickets() {
        if (_state.value.isLoadingTickets || _state.value.isRefreshingTickets) return
        _state.update { it.copy(isLoadingTickets = true, errorMessage = null) }
        fetchMyTickets()
    }

    private fun refreshMyTickets() {
        if (_state.value.isLoadingTickets || _state.value.isRefreshingTickets) return
        _state.update { it.copy(isRefreshingTickets = true, errorMessage = null) }
        fetchMyTickets()
    }

    private fun fetchMyTickets() {
        viewModelScope.launch {
            ticketRepository.getMyTickets()
                .onSuccess { ticketList ->
                    _state.update { currentState ->
                        val mappedTickets = mapTicketsToUiItems(ticketList, currentState.events)
                        currentState.copy(
                            rawTickets = ticketList,
                            myTickets = mappedTickets,
                            isLoadingTickets = false,
                            isRefreshingTickets = false
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.message ?: "Biletler yüklenemedi",
                            isLoadingTickets = false,
                            isRefreshingTickets = false
                        )
                    }
                }
        }
    }

    fun refreshActiveTab(tabIndex: Int) {
        if (tabIndex == 0) refreshEvents() else refreshMyTickets()
    }

    private fun mapTicketsToUiItems(tickets: List<Ticket>, events: List<Event>): List<TicketUiItem> {
        val groupedTickets = tickets.groupBy { it.ticketTypeId }
        return groupedTickets.map { (typeId, ticketsOfType) ->
            val matchedEvent = events.find { event ->
                event.ticketTypes.any { it.id == typeId }
            }
            val matchedTicketType = matchedEvent?.ticketTypes?.find { it.id == typeId }
            val displayTitle = matchedTicketType?.name ?: matchedEvent?.name ?: "Bilet Türü: ${typeId.take(5)}..."

            TicketUiItem(
                ticketTypeId = typeId,
                displayTitle = displayTitle,
                count = ticketsOfType.size
            )
        }
    }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}