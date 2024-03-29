package com.example.smsretriverstudy.domain

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays

object Test {
    fun hash(context: Context) {
        for (signature in getSignatures(context)) {
            hash(context.packageName, signature.toCharsString())
        }
    }

    private fun getSignatures(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        ).signingInfo?.run {
            if (hasMultipleSigners()) apkContentsSigners else signingCertificateHistory
        } ?: arrayOf()
    } else {
        TODO("VERSION.SDK_INT < P")
    }


    private fun hash(packageName: String, signature: String) =
        MessageDigest.getInstance("SHA-256").run {
            update("$packageName $signature".toByteArray(StandardCharsets.UTF_8))
            Base64.encodeToString(
                Arrays.copyOfRange(digest(), 0, 9),
                Base64.NO_PADDING or Base64.NO_WRAP
            ).substring(0, 11).also {
                Log.d("hash", String.format("pkg: %s -- hash: %s", packageName, it))
            }
        }
}