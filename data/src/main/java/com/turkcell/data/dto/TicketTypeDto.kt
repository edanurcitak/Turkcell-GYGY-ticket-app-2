package com.turkcell.data.dto

// Sunucudan gelen Bilet Türü formatı
data class TicketTypeDto(
    val id: String,
    val name: String,
    val priceCents: Int,
    val capacity: Int,
    val soldCount: Int,
    val remaining: Int
)