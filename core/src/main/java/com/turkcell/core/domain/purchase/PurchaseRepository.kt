package com.turkcell.core.domain.purchase

interface PurchaseRepository {
    suspend fun createPurchase(eventId: String, items: List<PurchaseItem>): Result<Purchase>
    suspend fun pay(purchaseId: String): Result<Purchase>
    suspend fun getPurchase(purchaseId: String): Result<Purchase>
}