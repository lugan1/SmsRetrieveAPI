package com.example.smsretriverstudy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smsretriverstudy.repository.AuthRepository
import com.example.smsretriverstudy.state.MainIntent
import com.example.smsretriverstudy.state.MainUiState
import com.example.smsretriverstudy.state.SmsAuthFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    val authForm = SmsAuthFrom()

    private val _state: MutableStateFlow<MainUiState> = MutableStateFlow(MainUiState.Idle)
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainUiState.Idle
        )

    fun sendIntent(intent: MainIntent) {
        when(intent) {
            is MainIntent.CodeChanged -> {
                authForm.code = intent.code
            }
            is MainIntent.PhoneNumberChanged -> {
                authForm.phoneNumber = intent.phoneNumber
            }
            MainIntent.SendSms -> {
                sendSMS()
            }
            MainIntent.VerifySms -> {
                smsVerify()
            }
        }
    }


    private fun sendSMS() {
        viewModelScope.launch {
            _state.value = MainUiState.Loading
            repository.sendSMSForNonExistentId(authForm.phoneNumber)
                .catch { e->
                    e.printStackTrace()
                    _state.value = MainUiState.Idle
                }
                .collect { _state.value = MainUiState.SmsSent }
        }
    }

    private fun smsVerify() {
        viewModelScope.launch {
            _state.value = MainUiState.Loading
            repository.smsVerify(authForm.phoneNumber, authForm.code)
                .catch {
                    e-> e.printStackTrace()
                    _state.value = MainUiState.Idle
                }
                .collect { _state.value = MainUiState.SmsVerified }
        }
    }
}