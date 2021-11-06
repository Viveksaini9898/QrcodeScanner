package com.qr.scanner.interfaces

import com.qr.scanner.model.Result

interface OnItemClickListener {
    fun onItemClick(result: Result)
}