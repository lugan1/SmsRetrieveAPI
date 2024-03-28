package com.example.smsretriverstudy.state

sealed interface MainUiState {
    data object Idle : MainUiState

    data object Loading : MainUiState

    data object SmsSent : MainUiState

    data object SmsVerified : MainUiState
}