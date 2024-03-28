package com.example.smsretriverstudy.repository

import com.example.smsretriverstudy.data.dto.SmsVerifyDto
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendSMSForNonExistentId(phone: String) : Flow<Unit>

    suspend fun smsVerify(phone: String, code: String) : Flow<Unit>
}