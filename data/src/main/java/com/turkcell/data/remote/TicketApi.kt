package com.turkcell.data.remote

import com.turkcell.data.dto.TicketDto
import retrofit2.http.GET

interface TicketApi {
    @GET("/me/tickets")
    suspend fun getMyTickets(): List<TicketDto>
}