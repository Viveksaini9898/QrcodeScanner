package com.qr.scanner.extension

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}