package com.turkcell.data.repository.purchase

import com.turkcell.core.domain.purchase.Purchase
import com.turkcell.core.domain.purchase.PurchaseItem
import com.turkcell.core.domain.purchase.PurchaseRepository
import com.turkcell.core.domain.purchase.PurchaseStatus
import com.turkcell.data.dto.purchase.CreatePurchaseRequestDto
import com.turkcell.data.dto.purchase.PurchaseItemRequestDto
import com.turkcell.data.remote.PurchaseApi
import com.turkcell.data.util.runCatchingApi

class PurchaseRepositoryImpl(
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {

    override suspend fun createPurchase(eventId: String, items: List<PurchaseItem>): Result<Purchase> = runCatchingApi {
        val requestDto = CreatePurchaseRequestDto(
            items = items.map { PurchaseItemRequestDto(it.ticketTypeId, it.quantity) }
        )
        purchaseApi.createPurchase(requestDto)
    }.map { dto ->
        Purchase(
            id = dto.id,
            status = runCatching { PurchaseStatus.valueOf(dto.status) }.getOrDefault(PurchaseStatus.PENDING),
            items = dto.items.map { PurchaseItem(it.ticketTypeId, it.quantity) }
        )
    }

    override suspend fun pay(purchaseId: String): Result<Purchase> = runCatchingApi {
        purchaseApi.pay(purchaseId)
    }.map { dto ->
        Purchase(
            id = dto.id,
            status = runCatching { PurchaseStatus.valueOf(dto.status) }.getOrDefault(PurchaseStatus.PENDING),
            items = dto.items.map { PurchaseItem(it.ticketTypeId, it.quantity) }
        )
    }

    override suspend fun getPurchase(purchaseId: String): Result<Purchase> = runCatchingApi {
        purchaseApi.getPurchase(purchaseId)
    }.map { dto ->
        Purchase(
            id = dto.id,
            status = runCatching { PurchaseStatus.valueOf(dto.status) }.getOrDefault(PurchaseStatus.PENDING),
            items = dto.items.map { PurchaseItem(it.ticketTypeId, it.quantity) }
        )
    }
}