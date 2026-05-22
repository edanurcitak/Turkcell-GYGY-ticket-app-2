package com.turkcell.core.domain.event

interface EventRepository {
    // Sunucudan (veya lokalden) bir Etkinlik listesi getirme sözleşmesi
    suspend fun getEvents(): Result<List<Event>>
}