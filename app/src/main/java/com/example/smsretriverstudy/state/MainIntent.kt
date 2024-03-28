package com.example.smsretriverstudy.state

sealed interface MainIntent {
    data class PhoneNumberChanged(val phoneNumber: String) : MainIntent

    data class CodeChanged(val code: String) : MainIntent

    data object SendSms : MainIntent

    data object VerifySms : MainIntent
}