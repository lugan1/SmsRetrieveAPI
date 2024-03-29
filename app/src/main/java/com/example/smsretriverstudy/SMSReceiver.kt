package com.example.smsretriverstudy

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class SMSReceiver : BroadcastReceiver() {
    companion object {
        private const val SMS_PATTERN = "^<#>.*\\[Smaple\\].+\\[(\\d{6})\\].+\$"
    }

    private var otpReceiver: OtpReceiver? = null

    val receiveFlow: MutableSharedFlow<String> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("SMSReceiver", "onReceive action: ${intent.action} retriever: ${SmsRetriever.SMS_RETRIEVED_ACTION}")
        if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
        if (intent.extras == null) return

        val bundle = requireNotNull(intent.extras)
        val status = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
        }
        else {
            bundle.get(SmsRetriever.EXTRA_STATUS) as Status
        }


        when(status?.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                // 문자메세지
                val message = bundle.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                Log.e("SMSReceiver", "message 수신: $message")
                if(message.isNullOrEmpty()) return

                //todo: code 파싱

                // View에 code 전송
                receiveFlow.tryEmit(message)
            }

            CommonStatusCodes.TIMEOUT -> {
                //todo: 타임아웃의 처리
                Log.e("SMSReceiver", "타임아웃 발생: CommonStateCodes.TIMEOUT")
            }

            else -> {
                Log.e("SMSReceiver", "else 발생: ${status?.statusCode}")
            }
        }
    }

    fun setOtpListener(receiver: OtpReceiver) {
        this.otpReceiver = receiver
    }

    fun doFilter() = IntentFilter().apply {
        addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
    }

    interface OtpReceiver {
        fun onOtpReceived(otp: String)
    }
}