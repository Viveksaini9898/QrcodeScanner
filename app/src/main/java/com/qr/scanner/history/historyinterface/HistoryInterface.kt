package com.qr.scanner.history.historyinterface

import android.app.Activity
import android.content.Context
import com.core.Result
import com.qr.scanner.result.ResultHandler

interface HistoryInterface<T> {
    fun add(context: Context?, result: Result?)
    fun clearAll(context: Context??)
    fun delete(context: Context??,number: Int)
    fun getAll(context: Context??): List<T>?
}