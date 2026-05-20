package com.turkcell.data.dto

// Sunucudan gelen Etkinlik formatı
data class EventDto(
    val id: String,
    val name: String,
    val description: String,
    val venue: String,
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketTypeDto>
)