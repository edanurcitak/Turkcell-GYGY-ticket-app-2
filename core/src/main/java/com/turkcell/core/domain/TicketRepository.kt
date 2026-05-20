package com.turkcell.core.domain

interface TicketRepository {
    // Kullanıcının satın aldığı biletleri getirme sözleşmesi
    suspend fun getMyTickets(): Result<List<Ticket>>
}