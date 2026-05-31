package com.turkcell.core.util

fun getAuthErrorMessage(errorCode: Int?): String {
    return when (errorCode) {
        400 -> "Geçersiz email veya şifre formatı"
        401 -> "Email veya şifre hatalı"
        403 -> "Bu işlemi yapmaya yetkiniz yok."
        409 -> "Bu email zaten kayıtlı"
        in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
}

fun getPurchaseErrorMessage(errorCode: Int?, serverMessage: String?): String {
    val messageText = serverMessage ?: ""

    return when {
        errorCode == 409 && messageText.contains("capacity_exceeded") -> "Stok yetersiz, lütfen etkinlik sayfasını yenileyin."
        errorCode == 409 && messageText.contains("already_paid") -> "Bu siparişin ödemesi zaten yapılmış."
        errorCode == 403 && messageText.contains("not_purchase_owner") -> "Bu işlemi yapmaya yetkiniz yok."
        errorCode == 400 -> "Geçersiz işlem (400): $messageText"
        errorCode == 401 -> "Oturum süreniz dolmuş, lütfen tekrar giriş yapın."
        else -> "Beklenmeyen bir hata oluştu. Kod: $errorCode"
    }
}

const val NETWORK_ERROR_MESSAGE = "İnternet bağlantısı yok"
const val UNKNOWN_ERROR_MESSAGE = "Bilinmeyen bir hata oluştu."