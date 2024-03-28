package com.example.smsretriverstudy.data

import com.squareup.moshi.Moshi

class MoshiHolder private constructor() {
    private object Holder {
        val INSTANCE: Moshi = Moshi.Builder()
            .build()
    }

    companion object {
        val instance: Moshi by lazy { Holder.INSTANCE }
    }
}
