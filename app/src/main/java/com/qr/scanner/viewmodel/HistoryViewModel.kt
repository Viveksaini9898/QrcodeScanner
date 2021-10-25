package com.qr.scanner.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qr.scanner.history.entity.History
import com.qr.scanner.history.SQLite.ORM.HistoryORM

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext


    private val users: MutableLiveData<List<History>> by lazy {
        MutableLiveData<List<History>>().also {
            loadUsers(context)
        }
    }

    fun getUsers(): LiveData<List<History>> {
        return users
    }

     fun loadUsers(context: Context?) {

        HistoryORM().getAll(context)
    }
}