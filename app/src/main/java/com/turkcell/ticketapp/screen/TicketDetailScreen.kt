package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TicketDetailScreen(
    ticketTypeId: String, // İsim güncellendi
    viewModel: TicketDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = ticketTypeId) {
        viewModel.loadTicketsByType(ticketTypeId)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.errorMessage != null) {
                Text(text = "Hata: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            } else if (state.tickets.isNotEmpty()) {

                // YENİ: Alt alta listelemek için LazyColumn kullanıyoruz
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Kartlar arası boşluk
                ) {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "🎟 Bilet Detayları", style = MaterialTheme.typography.headlineMedium)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }

                    // O türe ait kaç bilet varsa hepsini alt alta kart olarak çizer
                    items(state.tickets) { ticket ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "Bilet ID: ${ticket.id}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "QR Kodu: ${ticket.qrCode}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}