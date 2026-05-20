package com.turkcell.ticketapp.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.turkcell.ticketapp.viewmodel.HomePageViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(
    viewModel: HomePageViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Etkinlikler", "Biletlerim")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sekmelerin çizimi
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Hata varsa ekranda göster
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Seçili sekmeye göre listeleri göster
            when (selectedTabIndex) {
                0 -> {
                    // Etkinlikler Sekmesi
                    if (state.isLoadingEvents) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.events) { event ->
                                // Her bir etkinlik için kart çizimi
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = event.name, style = MaterialTheme.typography.titleMedium)
                                        Text(text = event.venue, style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Başlangıç: ${event.startsAt}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Biletlerim Sekmesi
                    if (state.isLoadingTickets) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.myTickets) { ticket ->
                                // Her bir bilet için kart çizimi
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "Bilet Kodu: ${ticket.id}", style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Durum: ${ticket.status}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "QR: ${ticket.qrCode}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}