package com.qr.scanner.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.qr.scanner.history.History

object ListAdapterCallBack : DiffUtil.ItemCallback<History>() {
    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
        return oldItem == newItem
    }
}