package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.turkcell.ticketapp.screen.*


// --- 1. NORMAL KULLANICI EKRANLARI ---
@Composable
private fun UserNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomePage) {
        composable<HomePage> {
            HomePageScreen(
                onTicketClick = { selectedTypeId -> navController.navigate(TicketDetail(ticketTypeId = selectedTypeId)) },
                onEventClick = { eventId -> navController.navigate(EventDetail(id = eventId)) }
            )
        }
        composable<EventDetail> { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<EventDetail>()
            EventDetailScreen(
                eventId = detailRoute.id,
                onNavigateBack = { navController.popBackStack() },
                onPurchaseSuccess = { navController.popBackStack(HomePage, inclusive = false) }
            )
        }
        composable<TicketDetail> { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<TicketDetail>()
            TicketDetailScreen(
                ticketTypeId = detailRoute.ticketTypeId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

// --- 2. GÖREVLİ (STAFF) EKRANLARI ---
@Composable
private fun StaffNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = StaffHome) {
        composable<StaffHome> {
            StaffScreen()
        }
    }
}

// --- 3. ADMİN EKRANLARI ---
@Composable
private fun AdminNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AdminHome) {
        composable<AdminHome> {
            // TODO: ADMIN SCREEN
        }
    }
}


@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}