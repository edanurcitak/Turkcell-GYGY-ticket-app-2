package com.turkcell.ticketapp.viewmodel

import com.turkcell.core.domain.event.Event

data class EventDetailState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val errorMessage: String? = null,
    val ticketCounts: Map<String, Int> = emptyMap(),

    val isPurchaseLoading: Boolean = false, // Satın alma tuşuna basıldığındaki yükleme
    val showPaymentDialog: Boolean = false, // Ödeme diyaloğu açık mı?
    val pendingPurchaseId: String? = null,  // Oluşturulan siparişin ID'si
    val isPaymentSuccessful: Boolean = false, // Ödeme başarılı oldu mu?
    val showSuccessDialog: Boolean = false
) {
    val totalPriceCents: Int
        get() = event?.ticketTypes?.sumOf { ticketType ->
            val count = ticketCounts[ticketType.id] ?: 0
            count * ticketType.priceCents
        } ?: 0
}