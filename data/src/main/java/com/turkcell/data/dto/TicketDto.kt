package com.turkcell.data.dto

// Sunucudan gelen Satın Alınmış Bilet formatı
data class TicketDto(
    val id: String,
    val qrCode: String,
    val status: String,
    val ticketTypeId: String
)