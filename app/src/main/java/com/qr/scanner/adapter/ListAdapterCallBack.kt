package com.qr.scanner.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.qr.scanner.history.History
import com.qr.scanner.model.Result

object ListAdapterCallBack : DiffUtil.ItemCallback<Result>() {
    override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem == newItem
    }
}