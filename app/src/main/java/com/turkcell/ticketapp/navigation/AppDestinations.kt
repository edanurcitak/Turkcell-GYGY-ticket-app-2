package com.turkcell.ticketapp.navigation


import kotlinx.serialization.Serializable

@Serializable
object Login
@Serializable
object Register
@Serializable
object HomePage
@Serializable
data class TicketDetail(val ticketTypeId: String)
@Serializable
data class EventDetail(val id: String)