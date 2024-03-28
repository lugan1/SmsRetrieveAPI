package com.example.smsretriverstudy.data

import com.example.smsretriverstudy.data.dto.ErrorResponse
import com.example.smsretriverstudy.data.enumeration.ErrorCode
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import retrofit2.Response

inline fun <reified T> Response<out T>.errorCode() : ErrorCode {
    val response = errorResponse()
    return ErrorCode.fromCode(response?.code)
}

inline fun <reified T> Response<out T>.errorResponse() : ErrorResponse? {
    if(this.errorBody() == null) return null
    val rawBody = this.errorBody()!!
    val moshi: Moshi = MoshiHolder.instance
    val adapter: JsonAdapter<ErrorResponse> = moshi.adapter(ErrorResponse::class.java)
    return adapter.fromJson(rawBody.string())
}