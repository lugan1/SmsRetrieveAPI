package com.example.smsretriverstudy.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @field:Json(name = "code")
    val code: String,
    @field:Json(name = "errors")
    val errors: List<Error>,
    @field:Json(name = "message")
    val message: String
)
