package com.turkcell.ticketapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.RegisterScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    //authRepository: AuthRepository = koinInject()
)
{
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

        // Giriş Ekranı Rotası
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    // TODO: Homescreen'e geçiş
                    // navController.navigate(Home)
                },
                onNavigateToRegister = {
                    navController.navigate(Register)
                }
            )
        }

        // Kayıt Ol Ekranı Rotası
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true } // Geri tuşu karmaşasını önler
                    }
                },
                onNavigateToLogin = {
                    // Kullanıcı vazgeçip "Giriş yap"a basarsa Login'e dön
                    navController.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }
    }
}