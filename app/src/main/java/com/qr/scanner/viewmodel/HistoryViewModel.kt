package com.qr.scanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.qr.scanner.history.HistoryRepository
import com.qr.scanner.model.Result

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = HistoryRepository(application)
    private val allHistory = repository.getAllHistory()
    private val favorites = repository.getFavorites()
    private val generate = repository.getGenerate()
    // val allHistory: LiveData<History> = repository.allHistory.asLiveData()

    fun insert(result: Result?) {
        repository.insert(result)
    }

    fun update(result: Result?) {
        repository.update(result)
    }

    fun getAllHistory(): LiveData<List<Result>>? {
        return allHistory!!
    }

    fun getFavorite(): LiveData<List<Result>>? {
        return favorites!!
    }

    fun getGenerate(): LiveData<List<Result>>? {
        return generate!!
    }

}