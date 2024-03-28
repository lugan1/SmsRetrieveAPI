package com.example.smsretriverstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smsretriverstudy.state.MainIntent
import com.example.smsretriverstudy.state.MainUiState
import com.example.smsretriverstudy.state.SmsAuthFrom
import com.example.smsretriverstudy.ui.component.CustomLoadingBar
import com.example.smsretriverstudy.ui.theme.SMSRetriverStudyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SMSRetriverStudyTheme {
                val viewModel: MainViewModel = hiltViewModel()
                TestScreen(
                    state = viewModel.state.collectAsState().value,
                    form = viewModel.authForm,
                    sendIntent = { viewModel.sendIntent(it) }
                )
            }
        }
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


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        Button(onClick = { sendIntent(MainIntent.SendSms) }) {
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