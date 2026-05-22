package com.turkcell.data.dto.event

import kotlinx.serialization.Serializable

// Sunucudan gelen Bilet Türü formatı

@Serializable
data class TicketTypeDto(
    val id: String,
    val name: String,
    val priceCents: Int,
    val capacity: Int,
    val soldCount: Int,
    val remaining: Int
)