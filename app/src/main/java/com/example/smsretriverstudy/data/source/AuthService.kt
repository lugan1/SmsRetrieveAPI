package com.example.smsretriverstudy.data.source

import com.example.smsretriverstudy.data.dto.SendSmsDto
import com.example.smsretriverstudy.data.dto.SmsVerifyDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    companion object {
        const val SERVICE = "/auth"
    }

    @POST("$SERVICE/sms")
    suspend fun sendSMSForNoneExistentId(
        @Header("Accept-Language") language: String,
        @Body smsDto: SendSmsDto
    ) : Response<Unit>

    @PUT("$SERVICE/sms")
    suspend fun smsVerify(@Body phoneCheck: SmsVerifyDto) : Response<Unit>
}