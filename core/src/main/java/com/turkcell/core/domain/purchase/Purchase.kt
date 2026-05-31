package com.turkcell.core.domain.purchase

data class PurchaseItem(
    val ticketTypeId: String,
    val quantity: Int
)

data class Purchase(
    val id: String,
    val status: PurchaseStatus,
    val items: List<PurchaseItem>
)