package com.turkcell.core.domain.event

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val venue: String, // Mekan
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketType> // Bir etkinliğin birden fazla bilet türü olabilir (VIP, Standart vb.)
)