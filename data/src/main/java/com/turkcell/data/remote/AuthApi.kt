package com.turkcell.data.remote

import com.turkcell.data.dto.CredentialsDto
import com.turkcell.data.dto.TokenPairDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body body: CredentialsDto): TokenPairDto

}