package com.turkcell.data.repository

import com.turkcell.core.domain.event.Ticket
import com.turkcell.core.domain.event.TicketRepository
import com.turkcell.data.remote.TicketApi
import com.turkcell.data.util.runCatchingApi

class TicketRepositoryImpl(
    private val ticketApi: TicketApi
) : TicketRepository {

    override suspend fun getMyTickets(): Result<List<Ticket>> = runCatchingApi {

        ticketApi.getMyTickets()
    }.map { dtoList ->
        dtoList.map { dto ->
            Ticket(
                id = dto.id,
                qrCode = dto.qrCode,
                status = dto.status,
                ticketTypeId = dto.ticketTypeId
            )
        }
    }
}