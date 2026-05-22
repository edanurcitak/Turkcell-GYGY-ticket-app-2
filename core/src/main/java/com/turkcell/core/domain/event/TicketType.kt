package com.turkcell.core.domain.event

data class TicketType(
    val id: String,
    val name: String,
    val priceCents: Int, // Fiyat kuruş cinsinden dönüyor
    val capacity: Int,
    val soldCount: Int,
    val remaining: Int
)