package com.turkcell.core.domain.purchase

data class Ticket(
    val id: String,
    val qrCode: String,
    val status: String, // Örn: "VALID" (Geçerli), "USED" (Kullanıldı)
    val ticketTypeId: String
)