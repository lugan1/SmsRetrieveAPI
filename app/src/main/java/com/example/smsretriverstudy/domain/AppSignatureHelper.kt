package com.example.smsretriverstudy.domain

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Base64.NO_PADDING
import android.util.Base64.NO_WRAP
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

class AppSignatureHelper(context: Context) : ContextWrapper(context) {
    companion object {
        private val TAG: String = AppSignatureHelper::class.java.simpleName
        private const val HASH_TYPE = "SHA-256"
        private const val NUM_HASHED_BYTES = 9
        private const val NUM_BASE64_CHAR = 11
    }

    /**
     * Get all the app signatures for the current package
     * @return
     */
    fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()

        try {
            // Get all package signatures for the current package
            val packageName = packageName
            val packageManager = packageManager

            val signatures = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .signingInfo
                    .signingCertificateHistory
            }
            else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                    .signatures
            }

            // For each signature create a compatible hash
            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(String.format("%s", hash))
                }

                Log.e(TAG, "Hash 값 추출 (SMS 뒤에 추가해서 보내야된다.):  $hash")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to find package to obtain hash.", e)
        }
        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)

            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64

            val base64Hash: String = Base64
                .encodeToString(hashSignature, NO_PADDING or NO_WRAP)
                .substring(0, NUM_BASE64_CHAR)

            Log.e(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))

            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e)
        }
        return null
    }
}