package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
    onPurchaseSuccess: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isPaymentSuccessful) {
        if (state.isPaymentSuccessful) {
            onPurchaseSuccess()
        }
    }

    if (state.showPaymentDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPaymentDialog() },
            title = { Text(text = "Ödeme Onayı") },
            text = { Text(text = "Toplam ₺${state.totalPriceCents / 100.0} tutarındaki bilet alım işlemini onaylıyor musunuz?") },
            confirmButton = {
                Button(onClick = { viewModel.confirmPayment() }, enabled = !state.isPurchaseLoading) {
                    if (state.isPurchaseLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Onayla")
                    }
                }
            },
            dismissButton = { TextButton(onClick = { viewModel.dismissPaymentDialog() }) { Text("İptal") } }
        )
    }

    if (state.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSuccessDialog() },
            title = { Text(text = "İşlem Başarılı 🎉", color = MaterialTheme.colorScheme.primary) },
            text = { Text(text = "Biletleriniz başarıyla satın alındı! Biletlerim sekmesinden görüntüleyebilirsiniz.") },
            confirmButton = { Button(onClick = { viewModel.dismissSuccessDialog() }) { Text("Tamam") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlik Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Geri") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        bottomBar = {
            if (state.event != null) {
                BottomCheckoutBar(
                    totalPriceCents = state.totalPriceCents,
                    isLoading = state.isPurchaseLoading,
                    onBuyClick = { viewModel.startPurchase() }
                )
            }
        }
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refreshEvent(eventId) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {

                state.isLoading && !state.isRefreshing && state.event == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                state.errorMessage != null && state.event == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                state.event != null -> {
                    val event = state.event!!
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                        if (state.errorMessage != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                                ) {
                                    Text(
                                        text = state.errorMessage!!,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Text(text = event.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Tarih: ${event.startsAt} - ${event.endsAt}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Mekan: ${event.venue}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Açıklama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Bilet Türleri", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(event.ticketTypes) { ticketType ->
                            val currentCount = state.ticketCounts[ticketType.id] ?: 0
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = ticketType.name, fontWeight = FontWeight.Bold)
                                        Text(text = "Fiyat: ₺${ticketType.priceCents / 100.0}")
                                        Text(
                                            text = "Stok: ${ticketType.remaining} / ${ticketType.capacity}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (ticketType.remaining < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(onClick = { viewModel.updateTicketCount(ticketType.id, -1) }, enabled = currentCount > 0) {
                                            Text("-", style = MaterialTheme.typography.titleLarge)
                                        }
                                        Text(text = currentCount.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                        IconButton(onClick = { viewModel.updateTicketCount(ticketType.id, 1) }, enabled = currentCount < 20 && currentCount < ticketType.remaining) {
                                            Text("+", style = MaterialTheme.typography.titleLarge)
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomCheckoutBar(totalPriceCents: Int, isLoading: Boolean, onBuyClick: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Toplam Tutar", style = MaterialTheme.typography.bodyMedium)
                Text(text = "₺${totalPriceCents / 100.0}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onBuyClick,
                enabled = totalPriceCents > 0 && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Satın Al")
                }
            }
        }
    }
}