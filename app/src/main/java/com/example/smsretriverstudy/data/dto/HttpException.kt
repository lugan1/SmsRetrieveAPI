package com.example.smsretriverstudy.data.dto

import androidx.annotation.StringRes

data class HttpException(
    val status: Int,
    @StringRes val stringRes: Int,
    val param: String? = null,
    override val message: String
) : Exception(message)