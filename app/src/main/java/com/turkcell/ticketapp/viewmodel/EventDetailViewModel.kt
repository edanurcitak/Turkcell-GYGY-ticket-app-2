package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    fun loadEvent(id: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            eventRepository.getEvent(id)
                .onSuccess { event ->
                    _state.update { it.copy(isLoading = false, event = event) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Etkinlik yüklenemedi.")
                    }
                }
        }
    }

    fun updateTicketCount(ticketTypeId: String, delta: Int) {
        val currentEvent = _state.value.event ?: return
        val ticketType = currentEvent.ticketTypes.find { it.id == ticketTypeId } ?: return

        val currentCount = _state.value.ticketCounts[ticketTypeId] ?: 0

        val maxAllowed = minOf(20, ticketType.remaining)
        val newCount = (currentCount + delta).coerceIn(0, maxAllowed)

        _state.update { currentState ->
            currentState.copy(
                ticketCounts = currentState.ticketCounts + (ticketTypeId to newCount)
            )
        }
    }
}