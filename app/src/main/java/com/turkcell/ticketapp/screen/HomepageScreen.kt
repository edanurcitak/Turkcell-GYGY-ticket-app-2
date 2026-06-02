package com.turkcell.ticketapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
    viewModel: HomePageViewModel = koinViewModel(),
    onTicketClick: (String) -> Unit,
    onEventClick: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Etkinlikler", "Biletlerim")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Etkinlikler", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Çıkış Yap"
                        )
                    }
                },
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
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // UI sadece güncel duruma (state) bakar, karar vermez.
            val isRefreshing = if (selectedTabIndex == 0) state.isRefreshingEvents else state.isRefreshingTickets

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshActiveTab(selectedTabIndex) }, // Business logic ViewModel'a taşındı
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Sadece ilk yüklemede (refresh değilken) ortadaki çarkı göster
                        if (state.isLoadingEvents && !state.isRefreshingEvents) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (state.events.isEmpty() && !state.isLoadingEvents) {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Henüz etkinlik bulunmuyor.")
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.events) { event ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable { onEventClick(event.id) },
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
                        // Biletlerim Sekmesi - Sadece ilk yüklemede ortadaki çarkı göster
                        if (state.isLoadingTickets && !state.isRefreshingTickets) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (state.myTickets.isEmpty() && !state.isLoadingTickets) {
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Henüz biletiniz bulunmuyor.")
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.myTickets) { ticketItem ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable { onTicketClick(ticketItem.ticketTypeId) },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "🎟 ${ticketItem.displayTitle} (${ticketItem.count} Adet)",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Bilet detayları >",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
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
}