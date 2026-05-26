package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.viewmodel.EventDetailViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    // Sayfa açıldığında etkinliği yükle
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlik Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            // Sadece içerik yüklendiğinde ve bilet seçildiğinde alt barı göster
            if (state.event != null) {
                BottomCheckoutBar(
                    totalPriceCents = state.totalPriceCents,
                    onBuyClick = {
                        // Bir sonraki aşamada (Satın Alım) burayı dolduracağız
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. LOADING STATE
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // 2. ERROR STATE
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 3. EMPTY STATE (Yüklendi ama etkinlik bulunamadı)
                !state.isLoading && state.event == null && state.errorMessage == null -> {
                    Text(
                        text = "Etkinlik bulunamadı.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 4. CONTENT STATE
                state.event != null -> {
                    val event = state.event!!
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Etkinlik Üst Bilgileri
                        item {
                            Text(text = event.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Tarih formatlama işlemi (Ödevdeki DateFormatter.kt kuralı)
                            // Not: Eğer DateFormatter dosyan farklı bir format fonksiyonu kullanıyorsa burayı ona göre güncelle
                            Text(text = "Tarih: ${event.startsAt} - ${event.endsAt}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Mekan: ${event.venue}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Açıklama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(24.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Bilet Türleri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Bilet Türleri Listesi
                        items(event.ticketTypes) { ticketType ->
                            val currentCount = state.ticketCounts[ticketType.id] ?: 0

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = ticketType.name, fontWeight = FontWeight.Bold)
                                        // Kuruşu TL'ye çevir
                                        Text(text = "Fiyat: ₺${ticketType.priceCents / 100.0}")
                                        Text(
                                            text = "Stok: ${ticketType.remaining} / ${ticketType.capacity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (ticketType.remaining < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // +/- Butonları
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { viewModel.updateTicketCount(ticketType.id, -1) },
                                            enabled = currentCount > 0
                                        ) {
                                            Text("-", style = MaterialTheme.typography.titleLarge)
                                        }

                                        Text(text = currentCount.toString(), modifier = Modifier.padding(horizontal = 8.dp))

                                        IconButton(
                                            onClick = { viewModel.updateTicketCount(ticketType.id, 1) },
                                            enabled = currentCount < 20 && currentCount < ticketType.remaining
                                        ) {
                                            Text("+", style = MaterialTheme.typography.titleLarge)
                                        }
                                    }
                                }
                            }
                        }

                        // Alt barın üstünü örtmemesi için boşluk
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

// Alt kısımdaki Satın Alım Barı
@Composable
fun BottomCheckoutBar(totalPriceCents: Int, onBuyClick: () -> Unit) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Toplam Tutar", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "₺${totalPriceCents / 100.0}", // Kuruş -> TL dönüşümü
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onBuyClick,
                enabled = totalPriceCents > 0 // Sadece sepette ürün varsa buton aktif olsun
            ) {
                Text("Satın Al")
            }
        }
    }
}