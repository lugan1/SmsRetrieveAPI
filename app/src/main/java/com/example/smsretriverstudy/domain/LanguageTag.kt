package com.example.smsretriverstudy.domain

fun String.languageTagToLocale(): String {
    return split("-").first()
}