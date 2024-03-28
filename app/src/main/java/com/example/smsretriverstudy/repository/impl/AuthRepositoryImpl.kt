package com.example.smsretriverstudy.repository.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.smsretriverstudy.R
import com.example.smsretriverstudy.data.dto.HttpException
import com.example.smsretriverstudy.data.dto.SendSmsDto
import com.example.smsretriverstudy.data.dto.SmsVerifyDto
import com.example.smsretriverstudy.data.enumeration.ErrorCode
import com.example.smsretriverstudy.data.errorCode
import com.example.smsretriverstudy.data.errorResponse
import com.example.smsretriverstudy.data.source.AuthService
import com.example.smsretriverstudy.domain.languageTagToLocale
import com.example.smsretriverstudy.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: AuthService
): AuthRepository {
    override suspend fun sendSMSForNonExistentId(phone: String): Flow<Unit> = flow {
        val language = AppCompatDelegate.getApplicationLocales().toLanguageTags().languageTagToLocale()
        val result = service.sendSMSForNoneExistentId(
            language = language,
            smsDto = SendSmsDto(phone)
        )
        emit(result)
    }.map { result ->
        if(result.isSuccessful.not()) {
            val error = result.errorResponse()
            val message = error?.message ?: ""
            when(result.code()) {
                400 -> {
                    val errorCode = error?.code
                    if(errorCode == ErrorCode.EXCEEDED_SEND_SMS.code) {
                        val param = error.errors.first().reason
                        throw HttpException(
                            status = 400,
                            stringRes = R.string.exceeded_send_sms,
                            message = message,
                            param = param
                        )
                    }
                    else {
                        throw HttpException(
                            status = 400,
                            stringRes = R.string.bad_request,
                            message = message,
                        )
                    }
                }
                401 -> throw HttpException(
                    status = 401,
                    stringRes = R.string.sms_verify_failed,
                    message = message,
                )
                403 -> throw HttpException(
                    status = 403,
                    stringRes = R.string.login_auth_failed_blocked,
                    message = message,
                )
                409 -> throw HttpException(
                    status = 409,
                    stringRes = R.string.duplicate_user,
                    message = message,
                )
                else -> throw HttpException(
                    status = result.code(),
                    stringRes = R.string.internal_server_error,
                    message = message,
                )
            }
        }

        Unit
    }.flowOn(Dispatchers.IO)


    override suspend fun smsVerify(phone: String, code: String): Flow<Unit> = flow {
        val result = service.smsVerify(SmsVerifyDto(phone, code))
        emit(result)
    }.map { response ->
        if(response.isSuccessful.not()) {
            when(response.code()) {
                400 -> {
                    val errorCode = response.errorCode()
                    if(errorCode == ErrorCode.AUTH_CHECK_TIMEOUT) {
                        throw HttpException(
                            status = 400,
                            stringRes = R.string.auth_check_timeout,
                            message = errorCode.name,
                        )
                    }
                    else {
                        throw HttpException(
                            status = 400,
                            stringRes = R.string.bad_request,
                            message = errorCode.name,
                        )
                    }
                }
                401 -> throw HttpException(
                    status = 401,
                    stringRes = R.string.sms_verify_failed,
                    message = response.errorCode().name,
                )
                else -> throw HttpException(
                    status = response.code(),
                    stringRes = R.string.internal_server_error,
                    message = response.errorCode().name,
                )
            }
        }

        Unit
    }.flowOn(Dispatchers.IO)
}