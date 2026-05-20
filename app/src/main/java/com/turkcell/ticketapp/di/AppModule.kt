package com.turkcell.ticketapp.di

import com.turkcell.ticketapp.viewmodel.LoginViewModel
import com.turkcell.ticketapp.viewmodel.RegisterViewModel
import com.turkcell.ticketapp.viewmodel.HomePageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // viewModel
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel) // Kayıt olma ViewModel'ini Koin'e tanıttım
    viewModelOf(::HomePageViewModel)
}
