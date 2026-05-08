package com.turkcell.ticketapp.di

import com.turkcell.core.domain.AuthRepository
import com.turkcell.data.repository.AuthRepositoryImpl
import com.turkcell.ticketapp.ui.viewmodel.AuthViewModel // ViewModel'imizi import ettik
import org.koin.androidx.viewmodel.dsl.viewModel // Koin'in viewModel özelliğini import ettik
import org.koin.dsl.module

val appModule = module {

    // Kural 1: Biri AuthRepository isterse, AuthRepositoryImpl sınıfını ver.
    // Uygulama hafızasında bundan sadece 1 kopya tut, herkes aynı kopyayı kullansın (Singleton).
    single<AuthRepository> {
        // AuthRepositoryImpl dışarıdan bir 'authApi' istiyor.

        AuthRepositoryImpl(authApi = get())
    }

    // Kural 2: Biri AuthViewModel isterse, onu oluştur ve içine AuthRepository'yi koy!

    viewModel {
        AuthViewModel(authRepository = get())
    }

}

