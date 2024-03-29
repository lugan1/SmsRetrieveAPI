package com.example.smsretriverstudy.domain

class SmsParseUseCase {
    companion object {
        private const val PATTERN = "인증 코드 : (\\d{6})"
    }

    fun execute(message: String): String {
        val result = Regex(PATTERN)
            .find(message)
            ?.groupValues

        println(result)
        return result?.get(1) ?: ""
    }
}