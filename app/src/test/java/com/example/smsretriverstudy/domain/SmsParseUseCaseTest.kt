package com.example.smsretriverstudy.domain

import org.junit.Test


class SmsParseUseCaseTest {
    private val smsParseUseCase = SmsParseUseCase()
    private val message = "[Web발신]\n" +
            "<#> [inPHR TEMP 인증번호] \n" +
            "인증 코드는 아래과 같습니다. \n" +
            "인증 코드 : 028365\n" +
            "IudQ7yob8nZ"

    @Test
    fun execute() {
        val result = smsParseUseCase.execute(message)
        println(result)
        assert(result == "028365")
    }
}