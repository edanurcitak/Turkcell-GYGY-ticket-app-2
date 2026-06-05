package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.ticketapp.screen.*
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    val currentUserRole by authRepository.currentUserRole.collectAsStateWithLifecycle(initialValue = null)

    when (isLoggedIn) {
        null -> SplashScreen()
        false -> UnAuthedNavHost(navController)
        true -> {
            when (currentUserRole) {
                UserRole.USER -> UserNavHost(navController)
                UserRole.STAFF -> StaffNavHost(navController)
                UserRole.ADMIN -> AdminNavHost(navController)
                null -> SplashScreen() // Token var ama rol hafızadan saniyelik okunurken hata vermemesi için
            }
        }
    }
}

// --- 1. NORMAL KULLANICI (USER) YÖNLENDİRMELERİ ---
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

// --- 2. GÖREVLİ (STAFF) YÖNLENDİRMELERİ ---
@Composable
private fun StaffNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = StaffHome) {
        composable<StaffHome> {
            StaffScreen() // Görevli paneli açılır
        }
    }
}

// --- 3. YÖNETİCİ (ADMIN) YÖNLENDİRMELERİ ---
@Composable
private fun AdminNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AdminHome) {
        composable<AdminHome> {
            //TODO: ADMIN
        }
    }
}


// --- 5. YÜKLEME EKRANI ---
@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController){
    NavHost(navController=navController, startDestination = Login) {
        composable<Login>{
            LoginScreen(
                onLoginSuccess = {},
                onNavigateToRegister = {navController.navigate(Register)}
            )
        }
        composable<Register> {
            Text("Register Screen")
        }
    }
}