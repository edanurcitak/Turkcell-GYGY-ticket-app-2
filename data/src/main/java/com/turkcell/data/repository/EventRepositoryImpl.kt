package com.turkcell.data.repository

import com.turkcell.core.domain.event.Event
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.event.TicketType
import com.turkcell.data.remote.EventApi
import com.turkcell.data.util.runCatchingApi

class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {

    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi {
        eventApi.getEvents()
    }.map { dtoList ->
        dtoList.map { dto ->
            Event(
                id = dto.id,
                name = dto.name,
                description = dto.description,
                venue = dto.place,
                startsAt = dto.startsAt,
                endsAt = dto.endsAt,
                ticketTypes = dto.ticketTypes.map { ticketTypeDto ->
                    TicketType(
                        id = ticketTypeDto.id,
                        name = ticketTypeDto.name,
                        priceCents = ticketTypeDto.priceCents,
                        capacity = ticketTypeDto.capacity,
                        soldCount = ticketTypeDto.soldCount,
                        remaining = ticketTypeDto.remaining
                    )
                }
            )
        }
    }
}