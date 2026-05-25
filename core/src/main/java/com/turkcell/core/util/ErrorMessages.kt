package com.turkcell.core.util

fun getAuthErrorMessage(errorCode: Int?): String {
    return when (errorCode) {
        400 -> "Geçersiz email veya şifre formatı"
        401 -> "Email veya şifre hatalı"
        409 -> "Bu email zaten kayıtlı"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
}

const val NETWORK_ERROR_MESSAGE = "İnternet bağlantısı yok"
const val UNKNOWN_ERROR_MESSAGE = "Bilinmeyen bir hata oluştu."