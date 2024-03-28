package com.example.smsretriverstudy.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmsVerifyDto(
    @field:Json(name = "phoneNumber")
    val phoneNumber: String,

    @field:Json(name = "code")
    val code: String? =  null
)
