package com.qr.scanner.extension

fun Int?.orZero(): Int {
    return this ?: 0
}