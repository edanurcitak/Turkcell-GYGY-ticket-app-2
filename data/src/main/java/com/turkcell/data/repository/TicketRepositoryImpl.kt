package com.turkcell.data.repository

import com.turkcell.core.domain.purchase.Ticket
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

    override suspend fun getTicketById(id: String): Result<Ticket> {
        return try {

            val ticketDto = ticketApi.getTicketById(id)

            val ticket = Ticket(
                id = ticketDto.id,
                qrCode = ticketDto.qrCode,
                status = ticketDto.status,
                ticketTypeId = ticketDto.ticketTypeId
            )

            Result.success(ticket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}