package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turkcell.ticketapp.viewmodel.TicketDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = ticketId) {
        viewModel.loadTicketDetail(ticketId)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            else if (state.errorMessage != null) {
                Text(text = "Hata: ${state.errorMessage}", color = MaterialTheme.colorScheme.error)
            }

            else if (state.ticket != null) {
                val ticket = state.ticket!!
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🎟 Bilet Detayları", style = MaterialTheme.typography.headlineMedium)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(text = "Bilet ID: ${ticket.id}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "QR Kodu: ${ticket.qrCode}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}