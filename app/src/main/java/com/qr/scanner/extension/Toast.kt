package com.qr.scanner.extension

import android.content.Context
import android.widget.Toast

object Toast {
    fun toast(context: Context?, charSequence: CharSequence?) {
        Toast.makeText(context, charSequence, Toast.LENGTH_LONG).show()
    }
}