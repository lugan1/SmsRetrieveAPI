package com.example.smsretriverstudy

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smsretriverstudy.domain.AppSignatureHelper
import com.example.smsretriverstudy.domain.SmsParseUseCase
import com.example.smsretriverstudy.state.MainIntent
import com.example.smsretriverstudy.state.MainUiState
import com.example.smsretriverstudy.state.SmsAuthFrom
import com.example.smsretriverstudy.ui.component.CustomLoadingBar
import com.example.smsretriverstudy.ui.theme.SMSRetriverStudyTheme
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val smsReceiver = SMSReceiver()

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppSignatureHelper(this).getAppSignatures()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), RECEIVER_EXPORTED)
        }
        else {
            ContextCompat.registerReceiver(this, smsReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION), ContextCompat.RECEIVER_EXPORTED)
        }

        setContent {
            SMSRetriverStudyTheme {
/*                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "hash: $hash")
                }*/

                val viewModel: MainViewModel = hiltViewModel()
                LaunchedEffect(Unit) {
                    smsReceiver.receiveFlow
                        .map { SmsParseUseCase().execute(it) }
                        .collect { received -> viewModel.sendIntent(MainIntent.CodeChanged(received)) }
                }

                TestScreen(
                    state = viewModel.state.collectAsState().value,
                    form = viewModel.authForm,
                    sendIntent = { viewModel.sendIntent(it) }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }
}

@Composable
fun TestScreen(
    state: MainUiState = MainUiState.Idle,
    form: SmsAuthFrom = SmsAuthFrom(),
    sendIntent: (MainIntent) -> Unit = {}
) {
    when(state) {
        is MainUiState.Loading -> {
            CustomLoadingBar()
        }
        else -> Unit
    }

    val context = LocalContext.current


    LaunchedEffect(Unit) {

    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val phoneNumber = Identity.getSignInClient(context)
                .getPhoneNumberFromIntent(result.data)
                .run { replace("+82", "0") }
            Log.e("phoneNumber", "휴대폰 번호: $phoneNumber")
            val intent = MainIntent.PhoneNumberChanged(phoneNumber)
            sendIntent(intent)
        } catch (e: ApiException) {
            e.printStackTrace()
            Log.e("phoneNumber", "휴대폰 번호 조회 실패")
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            val request = GetPhoneNumberHintIntentRequest.builder().build()
            Identity.getSignInClient(context)
                .getPhoneNumberHintIntent(request)
                .addOnSuccessListener { result ->
                    try {
                        val intentSender: IntentSender = result.intentSender
                        launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }) {
            Text(text = "휴대폰 번호 조회")
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = form.phoneNumber,
            onValueChange = {
                sendIntent(MainIntent.PhoneNumberChanged(it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )

        if(state is MainUiState.SmsSent) {
            Spacer(modifier = Modifier.height(5.dp))
            Text("SMS 전송 완료")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            // SMS Retriever API 시작 (문자메세지 수신 대기: 5분간)
            SmsRetriever.getClient(context)
                .startSmsRetriever()
                .addOnSuccessListener {
                    Log.e("MainActivity", "SMS Retriever API 시작")
                    sendIntent(MainIntent.SendSms)
                }
                .addOnFailureListener { e ->
                    Log.e("MainActivity", "SMS Retriever API 실패")
                    e.printStackTrace()
                }
        }) {
            Text(text = "SMS 전송")
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = form.code,
            onValueChange = {
                sendIntent(MainIntent.CodeChanged(it))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        
        if(state is MainUiState.SmsVerified) {
            Spacer(modifier = Modifier.height(5.dp))
            Text("SMS 인증 완료")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = { sendIntent(MainIntent.VerifySms) }) {
            Text(text = "SMS 인증")
        }
    }
}

@Preview
@Composable
fun TestScreenPreview() {
    SMSRetriverStudyTheme {
        Surface {
            TestScreen()
        }
    }
}