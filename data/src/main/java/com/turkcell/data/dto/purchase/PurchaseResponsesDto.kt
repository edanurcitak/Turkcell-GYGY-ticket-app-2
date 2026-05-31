package com.turkcell.data.dto.purchase

import kotlinx.serialization.Serializable

@Serializable
data class PurchaseDto(
    val id: String,
    val status: String,
    val items: List<PurchaseItemDto>
)

@Serializable
data class PurchaseItemDto(
    val ticketTypeId: String,
    val quantity: Int
)

@Serializable
data class TicketDto(
    val id: String,
    val status: String,
    val qrCode: String
)