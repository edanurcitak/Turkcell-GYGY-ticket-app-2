package com.turkcell.ticketapp.viewmodel

import com.turkcell.core.domain.event.Event

data class EventDetailState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val errorMessage: String? = null,
    val ticketCounts: Map<String, Int> = emptyMap()
) {
    val totalPriceCents: Int
        get() = event?.ticketTypes?.sumOf { ticketType ->
            val count = ticketCounts[ticketType.id] ?: 0
            count * ticketType.priceCents
        } ?: 0
}