package com.turkcell.core.domain.event

interface TicketRepository {
    // Kullanıcının satın aldığı biletleri getir
    suspend fun getMyTickets(): Result<List<Ticket>>

    //Bilet detaylarını getir
    suspend fun getTicketById(id: String): Result<Ticket>
}