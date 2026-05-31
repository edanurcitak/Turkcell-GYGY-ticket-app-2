package com.turkcell.ticketapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.event.EventRepository
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.data.network.ApiException
import com.turkcell.core.util.getPurchaseErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventDetailViewModel(
    private val eventRepository: EventRepository,
    private val purchaseRepository: PurchaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventDetailState())
    val state = _state.asStateFlow()

    fun loadEvent(id: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            eventRepository.getEvent(id)
                .onSuccess { event ->
                    _state.update { it.copy(isLoading = false, event = event) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message ?: "Etkinlik yüklenemedi.") }
                }
        }
    }

    fun updateTicketCount(ticketTypeId: String, delta: Int) {
        val currentEvent = _state.value.event ?: return
        val ticketType = currentEvent.ticketTypes.find { it.id == ticketTypeId } ?: return

        val currentCount = _state.value.ticketCounts[ticketTypeId] ?: 0
        val maxAllowed = minOf(20, ticketType.remaining)
        val newCount = (currentCount + delta).coerceIn(0, maxAllowed)

        _state.update { currentState ->
            currentState.copy(ticketCounts = currentState.ticketCounts + (ticketTypeId to newCount))
        }
    }

    fun startPurchase() {
        val currentEvent = _state.value.event ?: return

        val selectedItems = _state.value.ticketCounts.filter { it.value > 0 }
            .map { PurchaseItem(ticketTypeId = it.key, quantity = it.value) }

        if (selectedItems.isEmpty()) return

        _state.update { it.copy(isPurchaseLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.createPurchase(currentEvent.id, selectedItems)
                .onSuccess { purchase ->
                    _state.update {
                        it.copy(isPurchaseLoading = false, pendingPurchaseId = purchase.id, showPaymentDialog = true)
                    }
                }
                .onFailure { error ->
                    val apiError = error as? ApiException
                    val message = getPurchaseErrorMessage(apiError?.code, apiError?.message)
                    _state.update { it.copy(isPurchaseLoading = false, errorMessage = message) }

                    if (apiError?.code == 409 && apiError.message?.contains("capacity_exceeded") == true) {
                        loadEvent(currentEvent.id)
                    }
                }
        }
    }

    fun confirmPayment() {
        val purchaseId = _state.value.pendingPurchaseId ?: return

        _state.update { it.copy(isPurchaseLoading = true, errorMessage = null) }

        viewModelScope.launch {
            purchaseRepository.pay(purchaseId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            isPurchaseLoading = false,
                            showPaymentDialog = false,
                            //isPaymentSuccessful = true,
                            showSuccessDialog = true)
                    }
                }
                .onFailure { error ->
                    val apiError = error as? ApiException
                    val message = getPurchaseErrorMessage(apiError?.code, apiError?.message)
                    _state.update { it.copy(isPurchaseLoading = false, errorMessage = message) }
                }
        }
    }

    fun dismissPaymentDialog() {
        _state.update { it.copy(
            showPaymentDialog = false,
            pendingPurchaseId = null
        ) }
    }

    fun dismissSuccessDialog() {
        _state.update {
            it.copy(showSuccessDialog = false, isPaymentSuccessful = true)
        }
    }
}