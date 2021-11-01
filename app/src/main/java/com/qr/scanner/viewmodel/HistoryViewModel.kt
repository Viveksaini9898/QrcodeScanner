package com.qr.scanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.qr.scanner.history.History
import com.qr.scanner.history.HistoryRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application)
    private val allHistory = repository.getAllHistory()
    private val favorites = repository.getFavorites()
    private val generate = repository.getGenerate()
    // val allHistory: LiveData<History> = repository.allHistory.asLiveData()

    fun insert(history: History?) {
        repository.insert(history)
    }

    fun getAllHistory(): LiveData<List<History>>? {
        return allHistory!!
    }

    fun getFavorite(): LiveData<List<History>>? {
        return favorites!!
    }

    fun getGenerate(): LiveData<List<History>>? {
        return generate!!
    }

}