package com.example.smsretriverstudy.data.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(
    @field:Json(name = "field")
    val `field`: String?,
    @field:Json(name = "reason")
    val reason: String,
    @field:Json(name = "value")
    val value: String
)