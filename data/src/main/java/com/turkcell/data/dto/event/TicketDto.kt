package com.turkcell.data.dto.event

import kotlinx.serialization.Serializable

// Sunucudan gelen Satın Alınmış Bilet formatı
@Serializable
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String
)