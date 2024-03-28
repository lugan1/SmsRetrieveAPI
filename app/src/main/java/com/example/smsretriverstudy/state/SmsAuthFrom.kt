package com.example.smsretriverstudy.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class SmsAuthFrom() {
    var phoneNumber: String by mutableStateOf("")
    var code by mutableStateOf("")
}
